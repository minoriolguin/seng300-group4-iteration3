import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.bag.AbstractReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBagDispenserBronze;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

public class PurchaseBagsTest {
	private Software checkout;
	private AbstractSelfCheckoutStation station;
	private PurchaseBags bags;
	@Before
    public void setUp() {
		station = new SelfCheckoutStationGold();
        checkout = Software.getInstance(station);
		
		WeightDiscrepancy weight = new WeightDiscrepancy(checkout);
		UpdateCart cart = new UpdateCart(checkout);
		PriceLookUpCode code = new PriceLookUpCode("11991");
		PLUCodedProduct product = new PLUCodedProduct(code, "test", 5);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code, product);
		ReusableBagDispenserBronze ReusableBagDispenser = new ReusableBagDispenserBronze(2);
		bags = new PurchaseBags(weight, cart, code, ReusableBagDispenser);
		
	}
	@Test
    public void BagHasBeenAdded() {
		
	}
	
	@Test
    public void NobagsAvalible() {
		
	}

}
