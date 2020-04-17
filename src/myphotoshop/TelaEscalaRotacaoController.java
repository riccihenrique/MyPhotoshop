
package myphotoshop;

import java.net.URL;
import java.util.ResourceBundle;
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

public class TelaEscalaRotacaoController implements Initializable {

    @FXML
    private Slider rotacao;
    @FXML
    private ImageView imgView;
    @FXML
    private Label lbRotacao;
    @FXML
    private Slider escala;
    @FXML
    private Label lbEscala;
    private Image nimg;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        TelaPrincipalController.escala = 0;
        TelaPrincipalController.rotacao = 0;
        escala.setValue(TelaPrincipalController.escala);
        rotacao.setValue(TelaPrincipalController.rotacao);
        
        nimg = OpenCV.escala(TelaPrincipalController.img, 0 , 0.1);
        imgView.setImage(nimg);
        imgView.setFitWidth(nimg.getWidth());
        imgView.setFitHeight(nimg.getHeight());        
        imgView.setX(imgView.getImage().getWidth() / 2);
        imgView.setLayoutY(imgView.getImage().getHeight()/ 2);
    }    

    @FXML
    private void evtAttValor(MouseEvent event) 
    {
        nimg = OpenCV.escala(TelaPrincipalController.img, rotacao.getValue(), escala.getValue());
        imgView.setImage(nimg);
        imgView.setFitWidth(nimg.getWidth());
        imgView.setFitHeight(nimg.getHeight());
        lbRotacao.setText(String.valueOf((int)rotacao.getValue()));
    }

    @FXML
    private void evtAplicar(ActionEvent event) 
    {
        TelaPrincipalController.alterar = true;       
        TelaPrincipalController.rotacao = rotacao.getValue();
        TelaPrincipalController.escala = escala.getValue();
        
        Stage stage = (Stage) lbEscala.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void evtCancelar(ActionEvent event) 
    {
        Stage stage = (Stage) lbEscala.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void evtAttValor1(MouseEvent event) 
    {
        nimg = OpenCV.escala(TelaPrincipalController.img, rotacao.getValue() , escala.getValue());
        imgView.setImage(nimg);
        imgView.setFitWidth(nimg.getWidth());
        imgView.setFitHeight(nimg.getHeight());
        lbEscala.setText(String.valueOf((int)escala.getValue()));
    }
    
}
