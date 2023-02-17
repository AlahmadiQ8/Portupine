module com.nbk.weyay.weyaydesktopclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    opens com.nbk.weyay.weyaydesktopclient to javafx.fxml;
    exports com.nbk.weyay.weyaydesktopclient;
}