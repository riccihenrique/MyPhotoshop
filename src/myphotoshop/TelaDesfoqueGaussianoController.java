package myphotoshop;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import myphotoshop.transformacoes.OpenCV;

public class TelaDesfoqueGaussianoController implements Initializable {

    @FXML
    private Slider slider;
    @FXML
    private ImageView imgView;
    
    private Image img_gaus;
    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TelaPrincipalController.valor_desfoque = 15;
        slider.setValue(TelaPrincipalController.valor_desfoque);
        img_gaus = OpenCV.suavizacao_gausiana(TelaPrincipalController.img, (int)slider.getValue());
        imgView.setImage(img_gaus);
        imgView.setFitWidth(img_gaus.getWidth());
        imgView.setFitHeight(img_gaus.getHeight());
        label.setText(String.valueOf((int)slider.getValue()));
        imgView.setX(imgView.getImage().getWidth() / 2);
        imgView.setLayoutY(imgView.getImage().getHeight()/ 2);
    }

    @FXML
    private void evtAttValor(MouseEvent event)
    {
        img_gaus = OpenCV.suavizacao_gausiana(TelaPrincipalController.img, (int)slider.getValue());
        imgView.setImage(img_gaus);
        imgView.setFitWidth(img_gaus.getWidth());
        imgView.setFitHeight(img_gaus.getHeight());
        label.setText(String.valueOf((int)slider.getValue()));
    }

    @FXML
    private void evtAplicar(ActionEvent event)
    {
        TelaPrincipalController.valor_desfoque = (int)slider.getValue();
        TelaPrincipalController.alterar = true;
        
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void evtCancelar(ActionEvent event)
    {
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }
    
}
