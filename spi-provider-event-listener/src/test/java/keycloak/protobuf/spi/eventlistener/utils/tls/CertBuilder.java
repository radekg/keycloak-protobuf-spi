package keycloak.protobuf.spi.eventlistener.utils.tls;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertBuilder {

    public CertBuilder(String algo) {
        this(new Date(), 30, algo);
    }

    public CertBuilder(int days, String algo) {
        this(new Date(), days, algo);
    }

    public CertBuilder(int days, String algo, String hostname) throws IOException {
        this(new Date(), days, algo,
                new GeneralNames(new GeneralName(GeneralName.dNSName, hostname)).getEncoded());
    }

    public CertBuilder(int days, String algo, InetAddress address) throws IOException {
        this(new Date(), days, algo,
                new GeneralNames(new GeneralName(GeneralName.iPAddress, new DEROctetString(address.getAddress()))).getEncoded());
    }

    public CertBuilder(Date from, String algo) {
        this(from, 30, algo);
    }

    public CertBuilder(Date from, int days, String algo) {
        this(from, days, algo, new byte[0]);
    }

    public CertBuilder(Date from, int days, String algo, String hostname) throws IOException {
        this(from, days, algo,
                new GeneralNames(new GeneralName(GeneralName.dNSName, hostname)).getEncoded());
    }

    public CertBuilder(Date from, int days, String algo, InetAddress address) throws IOException {
        this(from, days, algo,
                new GeneralNames(new GeneralName(GeneralName.iPAddress, new DEROctetString(address.getAddress()))).getEncoded());
    }

    private Date from;
    private int days;
    private byte[] subjectAltName;
    private String algo;

    public CertBuilder(Date from, int days, String algo, byte[] subjectAltName) {
        this.days = days;
        this.from = from;
        this.subjectAltName = subjectAltName;
        this.algo = algo;
    }

    public X509Certificate generate(String dn, KeyPair keyPair) throws CertIOException, OperatorCreationException, CertificateException {

        Security.addProvider(new BouncyCastleProvider());
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        X500Name name = new X500Name(dn);

        Date to = new Date(from.getTime() + days * 86400000L);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X509v3CertificateBuilder v3CertGen = new X509v3CertificateBuilder(name, sn, from, to, name, subPubKeyInfo);

        if (subjectAltName.length > 0) {
            v3CertGen.addExtension(Extension.subjectAlternativeName, false, subjectAltName);
        }

        ContentSigner signer = new JcaContentSignerBuilder(algo).setProvider("BC").build(keyPair.getPrivate());
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(v3CertGen.build(signer));
    }

}
