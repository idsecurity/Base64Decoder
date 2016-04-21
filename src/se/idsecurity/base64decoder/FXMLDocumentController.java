/*
 * Copyright (C) 2016 almu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.idsecurity.base64decoder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.SortedMap;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author almu
 */
public class FXMLDocumentController implements Initializable {
    @FXML private Stage stage;//http://stackoverflow.com/questions/16636286/how-to-call-functions-on-the-stage-in-javafxs-controller-file
    @FXML private TextArea txtEncoded;
    @FXML private TextArea txtDecoded;
    @FXML private ComboBox<String> encoding;
    @FXML private ComboBox<String> decoderType;
    private final FileChooser fc = new FileChooser();
    
    @FXML
    protected void handleDecodeToText(ActionEvent event) {
        txtDecoded.setText(decodeToString());
    }
    
    @FXML
    protected void handleDecodeToFile(ActionEvent event) {
        
        File file = fc.showSaveDialog(stage);
        
        if (file == null) {
            return;
        }
        fc.setInitialDirectory(file.getParentFile());
        decodeToFile(file);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        SortedMap<String, Charset> availableCharsets = Charset.availableCharsets();
        encoding.getItems().addAll(availableCharsets.keySet());
        encoding.getSelectionModel().select("UTF-8");
        SelectKeyComboBoxListener selectKeyComboBoxListener = new SelectKeyComboBoxListener(encoding);
        Platform.runLater(() -> decoderType.requestFocus());
    }   
    
    private Base64.Decoder getDecoder() {
        Base64.Decoder decoder = null;
        
        
        String selectedItem = decoderType.getSelectionModel().getSelectedItem();
        
        switch (selectedItem) {
            case "Basic":
                decoder = Base64.getDecoder();
                break;
            case "MIME":
                decoder = Base64.getMimeDecoder();
                break;
            case "URL":
                decoder = Base64.getUrlDecoder();
                break;
        }
        return decoder;
    }
    
    private void decodeToFile(File file) {
        Objects.requireNonNull(file);
        Base64.Decoder decoder = getDecoder();
        try {
            byte[] decode = decoder.decode(txtEncoded.getText());
            try {
                Files.write(file.toPath(), decode);
                txtDecoded.setText("Success: " + file.getPath());
            } catch (IOException e) {
                txtDecoded.setText("Unable to save file. IO error:\n" + e);
            }
            
        } catch (IllegalArgumentException e) {
            txtDecoded.setText("Unable to save file. Decoding failed:\n" + e);
        }
        
    }
    
    private String decodeToString() {
        String decoded;
        
        try {
            byte[] decode = getDecoder().decode(txtEncoded.getText());
            decoded = new String(decode, encoding.getSelectionModel().getSelectedItem());
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            decoded = "Unable to decode:\n" + e;
        }       
        return decoded;
    }
}
