package rtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

public class Rtsp extends RtspDemo {
    public Rtsp(URI url, int rtpRcvPort) {
        super(url, rtpRcvPort);
    }

    public Rtsp(BufferedReader RTSPBufferedReader, BufferedWriter RTSPBufferedWriter) {
        super(RTSPBufferedReader, RTSPBufferedWriter);
    }

    @Override
    public boolean play() {
        // request is only valid if client is in correct state
        if (state != State.READY) {
            logger.log(Level.WARNING, "RTSP state: " + state);
            return false;
        }
        RTSPSeqNb++;  // increase RTSP sequence number for every RTSP request sent
        send_RTSP_request("PLAY");
        // Wait for the response
        logger.log(Level.INFO, "Wait for response...");
        if (parse_server_response() != 200) {
            logger.log(Level.WARNING, "Invalid Server Response");
            return false;
        } else {
            state = State.PLAYING;
            logger.log(Level.INFO, "New RTSP state: PLAYING\n");
            return true;
        }
    }

    @Override
    public boolean pause() {
        // request is only valid if client is in correct state
        if (state != State.PLAYING) {
            logger.log(Level.WARNING, "RTSP state: " + state);
            return false;
        }
        RTSPSeqNb++;  // increase RTSP sequence number for every RTSP request sent
        send_RTSP_request("PAUSE");
        // Wait for the response
        logger.log(Level.INFO, "Wait for response...");
        if (parse_server_response() != 200) {
            logger.log(Level.WARNING, "Invalid Server Response");
            return false;
        } else {
            state = State.READY;
            logger.log(Level.INFO, "New RTSP state: READY\n");
            return true;
        }
    }

    @Override
    public boolean teardown() {
        // request is only valid if client is in correct state
        if (state != State.PLAYING && state != State.READY) {
            logger.log(Level.WARNING, "RTSP state: " + state);
            return false;
        }
        RTSPSeqNb++;  // increase RTSP sequence number for every RTSP request sent
        send_RTSP_request("TEARDOWN");
        // Wait for the response
        logger.log(Level.INFO, "Wait for response...");
        if (parse_server_response() != 200) {
            logger.log(Level.WARNING, "Invalid Server Response");
            return false;
        } else {
            state = State.INIT;
            logger.log(Level.INFO, "New RTSP state: INIT\n");
            return true;
        }
    }

    @Override
    public void describe() {
        RTSPSeqNb++;  // increase RTSP sequence number for every RTSP request sent
        send_RTSP_request("DESCRIBE");
        logger.log(Level.INFO, "Wait for response...");

        if (parse_server_response() != 200) {
            logger.log(Level.WARNING, "Invalid Server Response");
        }
    }

    @Override
    public void options() {
        RTSPSeqNb++;  // increase RTSP sequence number for every RTSP request sent
        send_RTSP_request("OPTIONS");
        logger.log(Level.INFO, "Wait for response...");

        if (parse_server_response() != 200) {
            logger.log(Level.WARNING, "Invalid Server Response");
        }
    }


    @Override
    public void send_RTSP_request(String request_type) {
        try {

            StringBuilder request = new StringBuilder();

            switch (request_type) {
                case "OPTIONS": //kann eig auch "raus"
                    request.append("OPTIONS * RTSP/1.0\r\n");
                    request.append("CSeq: ").append(RTSPSeqNb).append("\r\n");
                    break;

                case "DESCRIBE":
                    request.append("DESCRIBE ").append(url).append(" RTSP/1.0\r\n");
                    request.append("CSeq: ").append(RTSPSeqNb).append("\r\n");
                    request.append("Accept: application/sdp\r\n");
                    break;

                case "SETUP":
                    request.append("SETUP ").append(url).append("/trackID=0 RTSP/1.0\r\n");
                    request.append("CSeq: ").append(RTSPSeqNb).append("\r\n");
                    request.append("Transport: RTP/AVP;unicast;client_port=").append(RTP_RCV_PORT)
                            .append("-").append(RTP_RCV_PORT + 1).append("\r\n");
                    break;

                case "PLAY":
                    request.append("PLAY ").append(url).append(" RTSP/1.0\r\n");
                    request.append("CSeq: ").append(RTSPSeqNb).append("\r\n");
                    request.append("Session: ").append(RTSPid).append("\r\n");
                    break;

                case "PAUSE":
                    request.append("PAUSE ").append(url).append(" RTSP/1.0\r\n");
                    request.append("CSeq: ").append(RTSPSeqNb).append("\r\n");
                    request.append("Session: ").append(RTSPid).append("\r\n");
                    break;

                case "TEARDOWN":
                    request.append("TEARDOWN ").append(url).append(" RTSP/1.0\r\n");
                    request.append("CSeq: ").append(RTSPSeqNb).append("\r\n");
                    request.append("Session: ").append(RTSPid).append("\r\n");
                    break;

                default:
                    logger.log(Level.WARNING, "Invalid Request" + request_type);
                    return;
            }


            request.append("\r\n"); //Ende Anfrage


            logger.log(Level.INFO, "Sending:\n" + request.toString());
            RTSPBufferedWriter.write(request.toString());
            RTSPBufferedWriter.flush();


            RTSPSeqNb++;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send Request: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
