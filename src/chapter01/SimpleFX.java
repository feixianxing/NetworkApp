package chapter01;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;

public class SimpleFX extends Application {
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnOpen = new Button("加载");
    private Button btnSave = new Button("保存");

    // 待发送信息的文本框
    private TextField tfSend = new TextField();
    // 显示信息的文本区域
    private TextArea taDisplay = new TextArea();

    // IO对象
    private TextFileIO textFileIO = new TextFileIO();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
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
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);
        mainPane.setBottom(hBox);

        // 界面显示
        Scene scene = new Scene(mainPane, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 设置文本区域只读，且自动换行
        taDisplay.setEditable(false);
        taDisplay.setWrapText(true);

        // 按钮绑定点击事件
        // "退出"按钮
        btnExit.setOnAction(event -> {
            System.exit(0);
        });
        // "发送"按钮
        btnSend.setOnAction(event -> {
            String msg = tfSend.getText();
            sendText(msg);
            tfSend.clear();
        });
        // "保存"按钮
        btnSave.setOnAction(event -> {
            // 添加当前事件信息进行保存
            saveFile(
                    LocalDateTime.now().withNano(0)+"\n"+taDisplay.getText()+"\n"
            );
        });
        // "加载"按钮
        btnOpen.setOnAction(event -> {
            String msg = loadFile();
            if(msg != null){
                taDisplay.clear();
                taDisplay.setText(msg);
            }
        });

        // 监听键盘事件
        scene.setOnKeyPressed(event -> {
            if(event.isShiftDown()&&event.getCode()==KeyCode.ENTER){
                String msg = "echo: " + tfSend.getText();
                sendText(msg);
                tfSend.clear();
            }else if(event.getCode()==KeyCode.ENTER){
                String msg = tfSend.getText();
                sendText(msg);
                tfSend.clear();
            }
        });
    }

    // 发送方法
    private void sendText(String msg){
        // 添加到上方的文本区域
        taDisplay.appendText(msg + "\n");
    }

    // 保存文本到文件
    private void saveFile(String msg){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if(file==null){
            return;
        }
        textFileIO.append(file, msg);
    }

    // 从文件加载文本
    private String loadFile(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file==null){
            return null;
        }
        return textFileIO.load(file);
    }
}
