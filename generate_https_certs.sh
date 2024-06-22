gen_key(){
  keytool -genkeypair \
    -alias https_cert \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore keystore.p12 \
    -validity 3650 \
    -storepass qwe123 \
    -keypass qwe123 \
    -dname "CN=amazonas.com, OU=amazonas.com, O=amazonas.com, L=amazonas.com, S=amazonas.com, C=amazonas.com"
}

if [ ! -f backend/src/main/resources/keystore.p12 ]; then
  gen_key
  mv keystore.p12 backend/src/main/resources/keystore.p12
fi

if [ ! -f frontend/src/main/resources/keystore.p12 ]; then
  gen_key
  mv keystore.p12 frontend/src/main/resources/keystore.p12
fi