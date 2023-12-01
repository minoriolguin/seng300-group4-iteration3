import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.bag.AbstractReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.bag.ReusableBagDispenserBronze;
import com.jjjwelectronics.bag.ReusableBagDispenserGold;
import com.jjjwelectronics.bag.ReusableBagDispenserSilver;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import powerutility.PowerGrid;

public class PurchaseBagsTest {
	private Software checkout;
	private AbstractSelfCheckoutStation station;
	private PurchaseBags bags;
	private AbstractReusableBagDispenser ReusableBagDispenser;
	private PLUCodedProduct product;
	
	@Before
    public void setUp() throws OverloadedDevice {
		PowerGrid powerGrid = PowerGrid.instance();      
		station = new SelfCheckoutStationGold();
        checkout = Software.getInstance(station);
        station.plugIn(powerGrid);
        station.turnOn();
        station.getBaggingArea().plugIn(powerGrid);
        station.getBaggingArea().turnOn();
        
    	ReusableBagDispenser = new ReusableBagDispenserGold(2);
		ReusableBagDispenser.plugIn(powerGrid);
		ReusableBagDispenser.turnOn();
		WeightDiscrepancy weight = new WeightDiscrepancy(checkout);
		UpdateCart cart = new UpdateCart(checkout);
		PriceLookUpCode code = new PriceLookUpCode("11991");
		product = new PLUCodedProduct(code, "test", 5);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code, product);
		bags = new PurchaseBags(weight, cart, code, ReusableBagDispenser);
		checkout.scannerScale.addAnItem(new ReusableBag());
	}
	@Test
    public void BagHasBeenAdded() throws OverloadedDevice, EmptyDevice {
		ReusableBag bag1 = new ReusableBag();
		ReusableBag bag2 = new ReusableBag();
		ReusableBagDispenser.unload();
		System.out.println(ReusableBagDispenser.getQuantityRemaining());
		ReusableBagDispenser.load(bag1);
		ReusableBagDispenser.load(bag1);
		System.out.println(ReusableBagDispenser.getQuantityRemaining());
		bags.AddBagToOrder();
		//System.out.println(ReusableBagDispenser.unload().length);
		System.out.println(ReusableBagDispenser.getQuantityRemaining());
		assert(ReusableBagDispenser.getQuantityRemaining()== 1);
		assert(checkout.getProductsInOrder().containsKey(product));
		
		
	}
	
	@Test
    public void NobagsAvalible() throws EmptyDevice, OverloadedDevice {
		ReusableBag bag1 = new ReusableBag();
		ReusableBag bag2 = new ReusableBag();
		ReusableBagDispenser.load(bag1);
		ReusableBagDispenser.load(bag2);
		bags.AddBagToOrder();
		bags.AddBagToOrder();
		bags.AddBagToOrder();
		assert(ReusableBagDispenser.getQuantityRemaining() == 0);
	}

}
