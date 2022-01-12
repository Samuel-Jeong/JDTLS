package network.dtls.type;

import network.dtls.type.base.DtlsFormat;
import network.dtls.type.base.DtlsHandshakeCommonBody;

/**
 * - This message carries the keys exchange algorithm parameters
 *      that the client needs from the server in order to get the symmetric encryption working thereafter.
 * - It is optional, since not all key exchanges require the server explicitly sending this message.
 * - Actually, in most cases, the Certificate message is enough for the client
 *      to securely communicate a premaster key with the server.
 * - The format of those parameters depends exclusively on the selected CipherSuite,
 *      which has been previously set by the server via the ServerHello message.
 * - Same with DtlsClientKeyExchange
 */
public class DtlsServerKeyExchange extends DtlsFormat {

    public static final int LENGTH = DtlsHandshakeCommonBody.LENGTH + 128;

    private DtlsHandshakeCommonBody dtlsHandshakeCommonBody;
    private byte[] encryptedPreMasterSecretData;

    public DtlsServerKeyExchange(DtlsHandshakeCommonBody dtlsHandshakeCommonBody) {
        this.dtlsHandshakeCommonBody = dtlsHandshakeCommonBody;
    }

    public DtlsServerKeyExchange() {}

    public DtlsServerKeyExchange(byte[] data) {
        if (data.length >= LENGTH) {
            int index = 0;

            byte[] commonBodyData = new byte[DtlsHandshakeCommonBody.LENGTH];
            System.arraycopy(data, index, commonBodyData, 0, DtlsHandshakeCommonBody.LENGTH);
            dtlsHandshakeCommonBody = new DtlsHandshakeCommonBody(commonBodyData);
            index += commonBodyData.length;

            encryptedPreMasterSecretData = new byte[128];
            System.arraycopy(data, index, encryptedPreMasterSecretData, 0, 128);
        }
    }

    @Override
    public byte[] getData() {
        if (dtlsHandshakeCommonBody == null || encryptedPreMasterSecretData == null) { return null; }

        int index = 0;
        byte[] data = new byte[LENGTH];

        byte[] commonBodyData = dtlsHandshakeCommonBody.getData();
        System.arraycopy(commonBodyData, 0, data, index, DtlsHandshakeCommonBody.LENGTH);
        index += DtlsHandshakeCommonBody.LENGTH;

        System.arraycopy(encryptedPreMasterSecretData, 0, data, index, encryptedPreMasterSecretData.length);

        return data;
    }

    public DtlsHandshakeCommonBody getDtlsHandshakeCommonBody() {
        return dtlsHandshakeCommonBody;
    }

    public void setDtlsHandshakeCommonBody(DtlsHandshakeCommonBody dtlsHandshakeCommonBody) {
        this.dtlsHandshakeCommonBody = dtlsHandshakeCommonBody;
    }

    public byte[] getEncryptedPreMasterSecretData() {
        return encryptedPreMasterSecretData;
    }

    public void setEncryptedPreMasterSecretData(byte[] encryptedPreMasterSecretData) {
        this.encryptedPreMasterSecretData = encryptedPreMasterSecretData;
    }

}