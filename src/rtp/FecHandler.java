package rtp;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class FecHandler extends FecHandlerDemo {
    @Override
    boolean checkCorrection(int nr, HashMap<Integer, RtpPacket> mediaPackets) {
        // FEC packet nummer für das fehlende Paket
        if (!fecList.containsKey(nr)) {
            logger.log(Level.FINER, "No FEC information for packet " + nr);
            return false;
        }

        // Liste aller FECPakete>
        List<Integer> protectedPackets = fecList.get(nr);


        Integer fecSeqNr = fecNr.get(nr);
        if (fecSeqNr == null || !fecStack.containsKey(fecSeqNr)) {
            logger.log(Level.FINER, "No FEC packet available for packet " + nr);
            return false;
        }

        // fehlende pakete zählen
        int missingCount = 0;
        for (Integer seqNr : protectedPackets) {
            if (!mediaPackets.containsKey(seqNr)) {
                missingCount++;
                if (missingCount > 1) {
                    logger.log(Level.FINER, "Too many packets missing in group for packet " + nr);
                    return false;  // wenn mehr als ein paket weg: direkt raus
                }
            }
        }

        return missingCount == 1;
    }

    @Override
    RtpPacket correctRtp(int nr, HashMap<Integer, RtpPacket> mediaPackets) {

        if (!checkCorrection(nr, mediaPackets)) {
            return null;
        }

        // FEC für fehlendes Paket
        Integer fecSeqNr = fecNr.get(nr);
        FecPacket fecPacket = fecStack.get(fecSeqNr);
        List<Integer> protectedPackets = fecList.get(nr);


        for (Integer seqNr : protectedPackets) {
            if (mediaPackets.containsKey(seqNr)) {
                fecPacket.addRtp(mediaPackets.get(seqNr));
            }
        }

        // gibt fehlendes paket zurück
        return fecPacket.getLostRtp(nr);
    }

    public FecHandler(int size) {
        super(size);
    }

    public FecHandler(boolean useFec) {
        super(useFec);
    }
}
