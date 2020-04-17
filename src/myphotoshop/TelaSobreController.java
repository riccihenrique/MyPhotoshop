
package myphotoshop;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class TelaSobreController implements Initializable 
{

    @FXML
    private TextArea txInfo;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        if(TelaPrincipalController.arq != null)
        {
            txInfo.setText(TelaPrincipalController.arq.getAbsolutePath() + "\n" +                    
                TelaPrincipalController.arq.length() / 1024 + "kb\n");
        }
    }    

    @FXML
    private void evtFechar(ActionEvent event) 
    {        
        ((Button) event.getSource()).getScene().getWindow().hide();
    }
}
