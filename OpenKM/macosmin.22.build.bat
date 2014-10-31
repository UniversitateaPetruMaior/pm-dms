D:
cd d:\java\woks\pm-dms\OpenKM\
mvn -Dmaven.test.skip=true clean gwt:compile -Dgwt.extrajvmArgs=-Xmx1024m -Dgwt.module=com.openkm.frontend.Main install
