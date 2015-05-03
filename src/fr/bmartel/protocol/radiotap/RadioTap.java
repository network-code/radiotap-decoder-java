package fr.bmartel.protocol.radiotap;

import fr.bmartel.protocol.radiotap.inter.IRadioTapFrame;
import fr.bmartel.protocol.radiotap.inter.IRadiotapData;
import fr.bmartel.protocol.radiotap.inter.IRadiotapFlags;
import fr.bmartel.utils.ByteUtils;
import fr.bmartel.utils.RadioTapException;

/**
 * Define radio tap header values (total Radio Tap length : 2 Bytes)
 * 
 * 
 * struct ieee80211_radiotap_header {
        u_int8_t        it_version;     // set to 0
        u_int8_t        it_pad;
        u_int16_t       it_len;         // entire length
        u_int32_t       it_present;     // fields present
} __attribute__((__packed__));

 * @author Bertrand Martel
 * 
 */
public class RadioTap implements IRadioTapFrame {

	/** major version of the radiotap header is in use */
	private byte headerRevision = 0x00;

	/** * entire length of the radiotap data */
	private int headerLength = 0;

	private IRadiotapFlags flagList = null;
	
	/** * bitmask for values that will be present in radio tap payload */
	private byte[] presentFlags = null;

	/** object containing all properties decoded from radio tap payload */
	private RadioTapData radioTapData = null;

	/**
	 * Parse radio tap headers according to radio tap standard
	 * http://www.radiotap.org/
	 * 
	 * @param frame
	 */
	public RadioTap(byte[] frame) throws RadioTapException{
		try
		{
			if (frame.length > 7) {
				headerRevision = frame[0];
	
				headerLength = ByteUtils.convertByteArrayToInt(new byte[] { frame[3], frame[2] });
				
				presentFlags = new byte[] { frame[7], frame[6], frame[5], frame[4] };
	
				if (frame.length >= headerLength + 8) {
					// fill radio tap header payload according to header length
					byte[] radioTapPayload = new byte[headerLength - 8];
	
					if (headerLength > 8) {
						for (int i = 8; i < headerLength; i++) {
							radioTapPayload[i - 8] = frame[i];
						}
					}
					RadioTapFlags flags = new RadioTapFlags();
					radioTapData = flags.decode(presentFlags, radioTapPayload);
					this.flagList=flags;
				} else {
					throw new RadioTapException("An error occured while decoding radio tap frame");
				}
			} else {
				throw new RadioTapException("An error occured while decoding radio tap frame");
			}
		}
		catch (Exception e )
		{
			e.printStackTrace();
			throw new RadioTapException();
		}
	}


	@Override
	public int getRadiotapVersion() {
		return (headerRevision & 0xFF);
	}

	@Override
	public int getRadioTapDataLength() {
		return (headerLength & 0xFF);
	}

	@Override
	public IRadiotapData getRadioTapData() {
		return radioTapData;
	}

	@Override
	public IRadiotapFlags getRadioTapFlagList() {
		return flagList;
	}
}