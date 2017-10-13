import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.shimmerresearch.bluetooth.ShimmerBluetooth.BT_STATE;
import com.shimmerresearch.driver.BasicProcessWithCallBack;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.ShimmerMsg;
import com.shimmerresearch.driver.Configuration.Shimmer3;
import com.shimmerresearch.driverUtilities.ChannelDetails.CHANNEL_TYPE;
import com.shimmerresearch.pcDriver.ShimmerPC;

import info.monitorenter.gui.chart.IRangePolicy;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.util.Range;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ShimmerWriteLEDTest extends BasicProcessWithCallBack{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ShimmerPC mShimmer = new ShimmerPC("ShimmerDevice", true);
		
		mShimmer.connect("COM11", "");
		
		ShimmerWriteLEDTest stest = new ShimmerWriteLEDTest();
		stest.setWaitForData(mShimmer);
		
		JFrame frame = new JFrame();
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.getContentPane().setLayout(null);
		
	}

	@Override
	protected void processMsgFromCallback(ShimmerMsg shimmerMSG) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		  int ind = shimmerMSG.mIdentifier;

		  Object object = (Object) shimmerMSG.mB;

		if (ind == ShimmerPC.MSG_IDENTIFIER_STATE_CHANGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			
			if (callbackObject.mState == BT_STATE.CONNECTING) {
				
			} else if (callbackObject.mState == BT_STATE.CONNECTED) {
				//System.out.println("Connected");
			} else if (callbackObject.mState == BT_STATE.DISCONNECTED
//					|| callbackObject.mState == BT_STATE.NONE
					|| callbackObject.mState == BT_STATE.CONNECTION_LOST){
				System.out.println("Shimmer DISCONNECTED or CONNECTION LOST");				
			}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_NOTIFICATION_MESSAGE) {
			CallbackObject callbackObject = (CallbackObject)object;
			int msg = callbackObject.mIndicator;
			if (msg== ShimmerPC.NOTIFICATION_SHIMMER_FULLY_INITIALIZED){
				System.out.println("Shimmer FULLY INITIALIZED");
			}
			if (msg == ShimmerPC.NOTIFICATION_SHIMMER_STOP_STREAMING) {
				
			} else if (msg == ShimmerPC.NOTIFICATION_SHIMMER_START_STREAMING) {
				
			} else {}
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_DATA_PACKET) {
			System.out.println("Shimmer MSG_IDENTIFIER_DATA_PACKET");
		} else if (ind == ShimmerPC.MSG_IDENTIFIER_PACKET_RECEPTION_RATE_OVERALL) {
			
		}
	
	
		
		
	}
}
