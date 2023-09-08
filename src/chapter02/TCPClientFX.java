package chapter02;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TCPClientFX extends Application {
    // TCP连接
    private TCPClient tcpClient = null;

    // 按钮
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnConnect = new Button("连接");

    // ip输入框
    private TextField tfIP = new TextField();
    // port输入框
    private TextField tfPort = new TextField();
    // 待发送信息的文本框
    private TextField tfSend = new TextField();
    // 显示信息的文本区域
    private TextArea taDisplay = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        // 顶部ip和port输入框
        HBox header = new HBox();
        header.setAlignment(Pos.BASELINE_CENTER);
        header.setSpacing(10);
        header.setPadding(new Insets(20, 20, 0, 20));
        header.getChildren().addAll(
                new Label("IP地址："),
                tfIP,
                new Label("端口："),
                tfPort,
                btnConnect
        );
        mainPane.setTop(header);


        // 内容显示区域
        VBox vBox = new VBox();
        vBox.setSpacing(10);    // 各控件之间的间隔
        // VBox 面板中的内容距离四周的留空区域
        vBox.setPadding(new Insets(10, 20, 10, 20));
        vBox.getChildren().addAll(
                new Label("信息显示区："),
                taDisplay,
                new Label("信息输入区："),
                tfSend
        );
        // 设置显示信息区的文本区域可以纵向自动扩充范围
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);
        // 底部按钮区域
        HBox footer = new HBox();
        footer.setSpacing(10);
        footer.setPadding(new Insets(10, 20, 10, 20));
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.getChildren().addAll(btnSend, btnExit);
        mainPane.setBottom(footer);

        // 界面显示
        Scene scene = new Scene(mainPane, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 设置文本区域只读，且自动换行
        taDisplay.setEditable(false);
        taDisplay.setWrapText(true);

        // 按钮绑定点击事件
        // "连接"按钮
        btnConnect.setOnAction(event -> {
            String ip = tfIP.getText().trim();
            String port = tfPort.getText().trim();

            try{
                tcpClient = new TCPClient(ip, port);
                // 成功连接服务器，接受服务器发来的第一条欢迎信息
                String firstMsg = tcpClient.receive();
                taDisplay.appendText(firstMsg + "\n");
            }catch (Exception e){
                taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
            }
        });
        // "退出"按钮
        btnExit.setOnAction(event -> {
            close();
        });
        // "发送"按钮
        btnSend.setOnAction(event -> {
            sendMessage();
            tfSend.clear();
        });
        // 窗口右上角的关闭按钮
        primaryStage.setOnCloseRequest(event -> {
            close();
        });

        // 监听键盘事件
        scene.setOnKeyPressed(event -> {
            if(event.getCode()==KeyCode.ENTER){
                sendMessage();
                tfSend.clear();
            }
        });
    }

    // 发送方法
    private void sendMessage(){
        String msg = tfSend.getText();
        taDisplay.appendText("客户端发送：" + msg + "\n");
        tcpClient.send(msg);
        String serverMsg = tcpClient.receive();
        if(serverMsg == null){
            return;
        }
        taDisplay.appendText(serverMsg + "\n");
    }

    // 关闭客户端
    private void close(){
        if(tcpClient != null){
            // 向服务器发送关闭连接的约定信息
            tcpClient.send("bye");
            tcpClient.close();
        }
        System.exit(0);
    }
}
