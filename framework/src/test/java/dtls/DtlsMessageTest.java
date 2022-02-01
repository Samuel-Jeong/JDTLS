package dtls;

import dtls.certificate.Certificates;
import dtls.cipher.DtlsCipherSuite;
import dtls.cipher.DtlsCipherSuiteList;
import dtls.cipher.DtlsCipherSuiteType;
import dtls.compression.DtlsCompressionMethodType;
import dtls.packet.DtlsPacket;
import dtls.packet.base.DtlsContentType;
import dtls.packet.base.DtlsProtocolVersion;
import dtls.packet.handshake.DtlsEncryptedHandShake;
import dtls.packet.handshake.DtlsHandshake;
import dtls.packet.recordlayer.DtlsRecordHeader;
import dtls.packet.recordlayer.DtlsRecordLayer;
import dtls.type.*;
import dtls.type.base.DtlsFormat;
import dtls.type.base.DtlsHandshakeCommonBody;
import dtls.type.base.DtlsHandshakeType;
import dtls.type.base.DtlsRandom;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.module.ByteUtil;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

public class DtlsMessageTest {

    private static final Logger logger = LoggerFactory.getLogger(DtlsMessageTest.class);

    @Test
    public void test() {
        /////////////////////////////////////////////////////////
        // DtlsCipherSuiteList
        DtlsCipherSuiteList dtlsCipherSuiteList = createDtlsCipherSuiteListTest();
        Assert.assertNotNull(dtlsCipherSuiteList);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsClientHello
        DtlsClientHello dtlsClientHello = createDtlsClientHelloTest();
        Assert.assertNotNull(dtlsClientHello);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsHelloVerifyRequest
        DtlsHelloVerifyRequest dtlsHelloVerifyRequest = createDtlsHelloVerifyRequestTest();
        Assert.assertNotNull(dtlsHelloVerifyRequest);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsServerHello
        DtlsServerHello dtlsServerHello = createDtlsServerHelloTest();
        Assert.assertNotNull(dtlsServerHello);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsCertificate
        DtlsCertificate dtlsCertificate = createDtlsCertificateTest();
        Assert.assertNotNull(dtlsCertificate);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsServerKeyExchange
        DtlsServerKeyExchange dtlsServerKeyExchange = createDtlsServerKeyExchangeTest();
        Assert.assertNotNull(dtlsServerKeyExchange);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsServerHelloDone
        DtlsServerHelloDone dtlsServerHelloDone = createDtlsServerHelloDoneTest();
        Assert.assertNotNull(dtlsServerHelloDone);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsServerKeyExchange
        DtlsClientKeyExchange dtlsClientKeyExchange = createDtlsClientKeyExchangeTest();
        Assert.assertNotNull(dtlsClientKeyExchange);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsFinished
        DtlsFinished dtlsFinished = createDtlsFinishedTest();
        Assert.assertNotNull(dtlsFinished);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsHandshake with DtlsFinished
        // 1) OBJECT
        DtlsHandshakeCommonBody dtlsFinishedHandshakeCommonBody = createDtlsCommonBodyTest(
                new DtlsHandshakeType(DtlsHandshakeType.TLS_TYPE_FINISHED),
                0
        );
        DtlsHandshake dtlsHandshake = createDtlsHandshakeByObjectTest(dtlsFinishedHandshakeCommonBody, dtlsFinished);
        Assert.assertNotNull(dtlsHandshake);

        // 2) BYTE DATA
        dtlsHandshake = createDtlsHandshakeByByteDataTest(dtlsFinished.getData());
        Assert.assertNotNull(dtlsHandshake);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // EncryptedDtlsHandshake
        DtlsEncryptedHandShake dtlsEncryptedHandShake = createDtlsEncryptedHandshakeTest();
        Assert.assertNotNull(dtlsEncryptedHandShake);
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // DtlsPacket with DtlsClientHello
        DtlsHandshakeCommonBody dtlsClientHelloHandshakeCommonBody = createDtlsCommonBodyTest(
                new DtlsHandshakeType(DtlsHandshakeType.TLS_TYPE_CLIENT_HELLO),
                dtlsClientHello.getData().length
        );
        dtlsHandshake = createDtlsHandshakeByObjectTest(dtlsClientHelloHandshakeCommonBody, dtlsClientHello);
        Assert.assertNotNull(dtlsHandshake);

        // 1) DtlsRecordHeader
        byte[] dtlsHandshakeData = dtlsHandshake.getData();
        DtlsRecordHeader dtlsRecordHeader = createDtlsRecordHeaderTest(dtlsHandshakeData.length);

        // 2) DtlsHandshakeFactory
        // Already exists

        // 3) DtlsRecordLayer List
        List<DtlsRecordLayer> dtlsRecordLayerList = new ArrayList<>();
        DtlsRecordLayer dtlsRecordLayer = new DtlsRecordLayer(dtlsRecordHeader, dtlsHandshake);
        dtlsRecordLayerList.add(dtlsRecordLayer);

        // 4) DtlsPacket
        DtlsPacket dtlsPacket = createDtlsPacketTest(dtlsRecordLayerList);
        Assert.assertNotNull(dtlsPacket);
        /////////////////////////////////////////////////////////
    }

    public static DtlsRecordHeader createDtlsRecordHeaderTest(int recordLength) {
        DtlsRecordHeader dtlsRecordHeader = new DtlsRecordHeader(
                new DtlsContentType(DtlsContentType.TLS_TYPE_HANDSHAKE),
                new DtlsProtocolVersion(DtlsProtocolVersion.DTLS_1_0),
                0,
                0,
                recordLength
        );
        logger.debug("[DtlsTest][createDtlsRecordHeaderTest] DtlsRecordHeader: {}", dtlsRecordHeader);

        return dtlsRecordHeader;
    }

    public static DtlsPacket createDtlsPacketTest(List<DtlsRecordLayer> dtlsRecordLayerList) {
        DtlsPacket dtlsPacket = new DtlsPacket(dtlsRecordLayerList);
        logger.debug("[DtlsTest][createDtlsPacketTest] DtlsPacket: {}", dtlsPacket);

        return dtlsPacket;
    }

    public static DtlsHandshake createDtlsHandshakeByObjectTest(DtlsHandshakeCommonBody dtlsHandshakeCommonBody, DtlsFormat dtlsFormat) {
        DtlsHandshake dtlsHandshake = new DtlsHandshake(
                dtlsHandshakeCommonBody,
                dtlsFormat
        );
        logger.debug("[DtlsTest][createDtlsHandshakeByObjectTest] DtlsHandshake: {}", dtlsHandshake);

        return dtlsHandshake;
    }

    public static DtlsHandshake createDtlsHandshakeByByteDataTest(byte[] data) {
        DtlsHandshake dtlsHandshake = new DtlsHandshake(data);
        logger.debug("[DtlsTest][createDtlsHandshakeByByteDataTest] DtlsHandshake: {}", dtlsHandshake);

        return dtlsHandshake;
    }

    public static DtlsEncryptedHandShake createDtlsEncryptedHandshakeTest() {
        byte[] encryptedMessage = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

        DtlsEncryptedHandShake dtlsEncryptedHandShake = new DtlsEncryptedHandShake(encryptedMessage);
        logger.debug("[DtlsTest][createDtlsEncryptedHandshakeTest] DtlsEncryptedHandShake: {}", dtlsEncryptedHandShake);

        return dtlsEncryptedHandShake;
    }

    public static DtlsCipherSuiteList createDtlsCipherSuiteListTest() {
        List<DtlsCipherSuite> dtlsCipherSuiteLists = new ArrayList<>();
        dtlsCipherSuiteLists.add(new DtlsCipherSuite(DtlsCipherSuiteType.TLS_RSA_EXPORT_WITH_RC4_40_MD5));
        dtlsCipherSuiteLists.add(new DtlsCipherSuite(DtlsCipherSuiteType.TLS_RSA_WITH_RC4_128_MD5));
        dtlsCipherSuiteLists.add(new DtlsCipherSuite(DtlsCipherSuiteType.TLS_KRB5_EXPORT_WITH_RC4_40_SHA));
        dtlsCipherSuiteLists.add(new DtlsCipherSuite(DtlsCipherSuiteType.TLS_RSA_EXPORT1024_WITH_RC4_56_SHA));
        dtlsCipherSuiteLists.add(new DtlsCipherSuite(DtlsCipherSuiteType.TLS_PSK_WITH_RC4_128_SHA));
        dtlsCipherSuiteLists.add(new DtlsCipherSuite(DtlsCipherSuiteType.TLS_ECDH_RSA_WITH_RC4_128_SHA));
        dtlsCipherSuiteLists.add(new DtlsCipherSuite(DtlsCipherSuiteType.TLS_ECDHE_PSK_WITH_RC4_128_SHA));

        DtlsCipherSuiteList dtlsCipherSuiteList = new DtlsCipherSuiteList(dtlsCipherSuiteLists);
        //logger.debug("[DtlsTest][createDtlsCipherSuiteListTest] DtlsCipherSuiteList: {}", dtlsCipherSuiteList);

        // ENCODING
        byte[] dtlsCipherSuiteListData = dtlsCipherSuiteList.getData();
        Assert.assertNotNull(dtlsCipherSuiteListData);
        //logger.debug("[DtlsTest][createDtlsCipherSuiteListTest] DtlsCipherSuiteList byte data: {}", dtlsCipherSuiteListData);
        //logger.debug("[DtlsTest][createDtlsCipherSuiteListTest] DtlsCipherSuiteList byte data: {}", ByteUtil.byteArrayToHex(dtlsCipherSuiteListData));

        // DECODING
        dtlsCipherSuiteList = new DtlsCipherSuiteList(dtlsCipherSuiteListData);
        //logger.debug("[DtlsTest][createDtlsCipherSuiteListTest] DtlsCipherSuiteList: {}", dtlsCipherSuiteList);

        return dtlsCipherSuiteList;
    }

    public static DtlsHandshakeCommonBody createDtlsCommonBodyTest(DtlsHandshakeType dtlsHandshakeType, int length) {
        return new DtlsHandshakeCommonBody(
                new DtlsHandshakeType(dtlsHandshakeType.getType()),
                length, 0,
                0, length
        );
    }

    public static DtlsClientHello createDtlsClientHelloTest() {
        DtlsCipherSuiteList dtlsCipherSuiteList = createDtlsCipherSuiteListTest();
        DtlsClientHello dtlsClientHello = new DtlsClientHello(
                new DtlsProtocolVersion(DtlsProtocolVersion.DTLS_1_0),
                DtlsRandom.getRandom(),
                (short) 8,
                (short) 0, null,
                dtlsCipherSuiteList.getTotalLength(), dtlsCipherSuiteList,
                (short) 1, new DtlsCompressionMethodType(DtlsCompressionMethodType.TLS_COMPRESSION_METHOD_NULL)
        );
        logger.debug("[DtlsTest][createDtlsClientHelloTest] DtlsClientHello: {}", dtlsClientHello);

        // ENCODING
        byte[] dtlsClientHelloData = dtlsClientHello.getData();
        logger.debug("[DtlsTest][createDtlsClientHelloTest] DtlsClientHello byte data: {}", dtlsClientHelloData);
        logger.debug("[DtlsTest][createDtlsClientHelloTest] DtlsClientHello byte data: {}", ByteUtil.byteArrayToHex(dtlsClientHelloData));

        // DECODING
        dtlsClientHello = new DtlsClientHello(dtlsClientHelloData);
        logger.debug("[DtlsTest][createDtlsClientHelloTest] DtlsClientHello: {}", dtlsClientHello);

        return dtlsClientHello;
    }

    public static DtlsHelloVerifyRequest createDtlsHelloVerifyRequestTest() {
        DtlsHelloVerifyRequest dtlsHelloVerifyRequest = new DtlsHelloVerifyRequest(
                new DtlsProtocolVersion(DtlsProtocolVersion.DTLS_1_0),
                (short) 0, null
        );
        logger.debug("[DtlsTest][createDtlsHelloVerifyRequest] DtlsHelloVerifyRequest: {}", dtlsHelloVerifyRequest);

        // ENCODING
        byte[] dtlsHelloVerifyRequestData = dtlsHelloVerifyRequest.getData();
        logger.debug("[DtlsTest][createDtlsHelloVerifyRequest] DtlsHelloVerifyRequest byte data: {}", dtlsHelloVerifyRequestData);
        logger.debug("[DtlsTest][createDtlsHelloVerifyRequest] DtlsHelloVerifyRequest byte data: {}", ByteUtil.byteArrayToHex(dtlsHelloVerifyRequestData));

        // DECODING
        dtlsHelloVerifyRequest = new DtlsHelloVerifyRequest(dtlsHelloVerifyRequestData);
        logger.debug("[DtlsTest][createDtlsHelloVerifyRequest] DtlsHelloVerifyRequest: {}", dtlsHelloVerifyRequestData);

        return dtlsHelloVerifyRequest;
    }

    public static DtlsServerHello createDtlsServerHelloTest() {
        DtlsServerHello dtlsServerHello = new DtlsServerHello(
                DtlsRandom.getRandom(),
                (short) 8, new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 },
                new DtlsCipherSuite(DtlsCipherSuiteType.TLS_RSA_WITH_RC4_128_MD5),
                new DtlsCompressionMethodType(DtlsCompressionMethodType.TLS_COMPRESSION_METHOD_NULL)
        );

        logger.debug("[DtlsTest][createDtlsServerHelloTest] DtlsServerHello: {}", dtlsServerHello);

        // ENCODING
        byte[] dtlsHelloVedtlsServerHelloData = dtlsServerHello.getData();
        logger.debug("[DtlsTest][createDtlsServerHelloTest] DtlsServerHello byte data: {}", dtlsHelloVedtlsServerHelloData);
        logger.debug("[DtlsTest][createDtlsServerHelloTest] DtlsServerHello byte data: {}", ByteUtil.byteArrayToHex(dtlsHelloVedtlsServerHelloData));

        // DECODING
        dtlsServerHello = new DtlsServerHello(dtlsHelloVedtlsServerHelloData);
        logger.debug("[DtlsTest][createDtlsServerHelloTest] DtlsServerHello: {}", dtlsHelloVedtlsServerHelloData);

        return dtlsServerHello;
    }

    public static DtlsCertificate createDtlsCertificateTest() {
        byte[] certificateData;
        try {
            String certificateFileName = "/Users/jamesj/GIT_PROJECTS/JDTLS/framework/src/test/resources/dtls/certificate/x509v3_md5_rsa_certificate";
            FileInputStream fileInputStream = new FileInputStream(certificateFileName);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            Certificate certificate = certificateFactory.generateCertificate(fileInputStream);
            certificateData = certificate.getEncoded();

            PublicKey publicKey = certificate.getPublicKey();
            logger.debug("[DtlsTest][createDtlsCertificateTest] Public key: [{}], \ncertificate.getEncoded(): {}", publicKey, certificateData);
        } catch (Exception e) {
            logger.debug("[DtlsTest][createDtlsCertificateTest] Certificate Exception", e);
            return null;
        }

        Certificates certificates = new Certificates(
                certificateData.length,
                certificateData
        );
        byte[] certificatesData = certificates.getData();

        DtlsCertificate dtlsCertificate = new DtlsCertificate(
                certificatesData.length,
                certificates
        );

        logger.debug("[DtlsTest][createDtlsCertificateTest] DtlsCertificate: {}", dtlsCertificate);

        // ENCODING
        byte[] dtlsCertificateData = dtlsCertificate.getData();
        logger.debug("[DtlsTest][createDtlsCertificateTest] DtlsCertificate byte data: {}", dtlsCertificateData);
        logger.debug("[DtlsTest][createDtlsCertificateTest] DtlsCertificate byte data: {}", ByteUtil.byteArrayToHex(dtlsCertificateData));

        // DECODING
        dtlsCertificate = new DtlsCertificate(dtlsCertificateData);
        logger.debug("[DtlsTest][createDtlsCertificateTest] DtlsCertificate: {}", dtlsCertificate);

        return dtlsCertificate;
    }

    public static DtlsServerKeyExchange createDtlsServerKeyExchangeTest() {
        byte[] encryptedPreMasterSecretData = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

        DtlsServerKeyExchange dtlsServerKeyExchange = new DtlsServerKeyExchange(encryptedPreMasterSecretData);
        logger.debug("[DtlsTest][createDtlsServerKeyExchangeTest] DtlsServerKeyExchange: {}", dtlsServerKeyExchange);

        // ENCODING
        byte[] dtlsServerKeyExchangeData = dtlsServerKeyExchange.getData();
        logger.debug("[DtlsTest][createDtlsServerKeyExchangeTest] DtlsServerKeyExchange byte data: {}", dtlsServerKeyExchangeData);
        logger.debug("[DtlsTest][createDtlsServerKeyExchangeTest] DtlsServerKeyExchange byte data: {}", ByteUtil.byteArrayToHex(dtlsServerKeyExchangeData));

        // DECODING
        dtlsServerKeyExchange = new DtlsServerKeyExchange(dtlsServerKeyExchangeData);
        logger.debug("[DtlsTest][createDtlsServerKeyExchangeTest] DtlsServerKeyExchange: {}", dtlsServerKeyExchange);

        return dtlsServerKeyExchange;
    }

    public static DtlsServerHelloDone createDtlsServerHelloDoneTest() {
        DtlsServerHelloDone dtlsServerHelloDone = new DtlsServerHelloDone();
        logger.debug("[DtlsTest][createDtlsServerHelloDoneTest] DtlsServerHelloDone: {}", dtlsServerHelloDone);
        return dtlsServerHelloDone;
    }

    public static DtlsClientKeyExchange createDtlsClientKeyExchangeTest() {
        byte[] encryptedPreMasterSecretData = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

        DtlsClientKeyExchange dtlsClientKeyExchange = new DtlsClientKeyExchange(encryptedPreMasterSecretData);
        logger.debug("[DtlsTest][createDtlsClientKeyExchangeTest] DtlsClientKeyExchange: {}", dtlsClientKeyExchange);

        // ENCODING
        byte[] dtlsClientKeyExchangeData = dtlsClientKeyExchange.getData();
        logger.debug("[DtlsTest][createDtlsClientKeyExchangeTest] DtlsClientKeyExchange byte data: {}", dtlsClientKeyExchangeData);
        logger.debug("[DtlsTest][createDtlsClientKeyExchangeTest] DtlsClientKeyExchange byte data: {}", ByteUtil.byteArrayToHex(dtlsClientKeyExchangeData));

        // DECODING
        dtlsClientKeyExchange = new DtlsClientKeyExchange(dtlsClientKeyExchangeData);
        logger.debug("[DtlsTest][createDtlsClientKeyExchangeTest] DtlsClientKeyExchange: {}", dtlsClientKeyExchange);

        return dtlsClientKeyExchange;
    }

    public static DtlsFinished createDtlsFinishedTest() {
        DtlsFinished dtlsFinished = new DtlsFinished();
        logger.debug("[DtlsTest][createDtlsFinishedTest] DtlsFinished: {}", dtlsFinished);
        return dtlsFinished;
    }

}