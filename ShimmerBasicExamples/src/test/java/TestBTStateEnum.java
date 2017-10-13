import com.shimmerresearch.bluetooth.ShimmerBluetooth.BT_STATE;

public class TestBTStateEnum {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BT_STATE test1 = BT_STATE.CONNECTED;
		BT_STATE test2 = test1;
		
		test1 = BT_STATE.DISCONNECTED;

		TestBTStateEnum t = new TestBTStateEnum(); 
		t.initialize();		
	}
	
	public void initialize(){
		//radio
		BTSTATE a = new BTSTATE();

		//pass radio state to shimmer
		BTSTATE b = a;
		
		System.out.println(a.state.toString());
		System.out.println(b.state.toString());
		
		//radio state changes
		a.state = BT_STATE.DISCONNECTED;
		System.out.println(a.state.toString());
		System.out.println(b.state.toString());
		
		//state changed from shimmer device
		b.state = BT_STATE.CONNECTION_LOST;
		System.out.println(a.state.toString());
		System.out.println(b.state.toString());
	}
	
	public class BTSTATE{
		public BT_STATE state = BT_STATE.CONNECTED;
	}

}
