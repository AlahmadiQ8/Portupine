module com.nbk.weyay.weyaydesktopclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;
    requires kotlinx.coroutines.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.desktop;

    opens com.nbk.weyay.weyaydesktopclient to javafx.fxml;
    exports com.nbk.weyay.weyaydesktopclient;
}