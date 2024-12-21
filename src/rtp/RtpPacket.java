package rtp;

public class RtpPacket extends RtpPacketDemo{
    @Override
    void setRtpHeader() {

        header[0] = (byte)((Version << 6) | (Padding << 5) | (Extension << 4) | CC);
        
        // Second byte: [M P P P P P P P]
        // M: Marker (1 bit)
        // P: Payload Type (7 bits)
        header[1] = (byte)(
                (Marker << 7) |      // Place Marker in bit 7 (0x80)
                        PayloadType          // Place PayloadType in bits 6-0 (0x7F)
        );

        // Third and fourth bytes: 16-bit sequence number
        // Used for packet ordering and loss detection
        header[2] = (byte)(SequenceNumber >> 8);     // High byte of sequence number
        header[3] = (byte)(SequenceNumber);   // Low byte of sequence number

        // Next four bytes: 32-bit timestamp
        // Used for synchronization and jitter calculations
        header[4] = (byte)(TimeStamp >> 24);         // Highest byte of timestamp
        header[5] = (byte)(TimeStamp >> 16);         // Second byte of timestamp
        header[6] = (byte)(TimeStamp >> 8);          // Third byte of timestamp
        header[7] = (byte)(TimeStamp);        // Lowest byte of timestamp

        // Last four bytes: 32-bit SSRC (Synchronization Source Identifier)
        // Unique identifier for the source of the RTP stream
        header[8] = (byte)(Ssrc >> 24);              // Highest byte of SSRC
        header[9] = (byte)(Ssrc >> 16);              // Second byte of SSRC
        header[10] = (byte)(Ssrc >> 8);              // Third byte of SSRC
        header[11] = (byte)(Ssrc);            // Lowest byte of SSRC
    }

    public RtpPacket(int PType, int Framenb, int Time, int Mar, byte[] data, int data_length) {
        super(PType, Framenb, Time, Mar, data, data_length);
    }

    public RtpPacket(byte[] packet, int packet_size) {
        super(packet, packet_size);
    }
}
