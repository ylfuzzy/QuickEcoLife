package QuickEcoLife.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriverException;

import QuickEcoLife.model.AutomationModel;
import QuickEcoLife.util.ButtonTextFieldCombo;
import QuickEcoLife.util.ImageCombo;
import QuickEcoLife.util.MetadataExtractor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainViewController {
	@FXML
	private TextField tf_ID;
	@FXML
	private PasswordField tf_passwd;
	@FXML
	private Button btn_go;
	
	@FXML
	private Button btn_inspect_00;
	@FXML
	private TextField tf_inspect_00;
	@FXML
	private Button btn_inspect_04;
	@FXML
	private TextField tf_inspect_04;
	
	@FXML
	private Button btn_inspect_10;
	@FXML
	private TextField tf_inspect_10;
	@FXML
	private Button btn_inspect_14;
	@FXML
	private TextField tf_inspect_14;
	
	@FXML
	private Button btn_inspect_20;
	@FXML
	private TextField tf_inspect_20;
	@FXML
	private Button btn_inspect_24;
	@FXML
	private TextField tf_inspect_24;
	
	@FXML
	private Button btn_inspect_30;
	@FXML
	private TextField tf_inspect_30;
	@FXML
	private Button btn_inspect_34;
	@FXML
	private TextField tf_inspect_34;
	
	
	@FXML
	private Button btn_clear_00;
	@FXML
	private TextField tf_clear_00;
	@FXML
	private Button btn_clear_04;
	@FXML
	private TextField tf_clear_04;
	
	@FXML
	private Button btn_clear_10;
	@FXML
	private TextField tf_clear_10;
	@FXML
	private Button btn_clear_14;
	@FXML
	private TextField tf_clear_14;
	
	@FXML
	private Button btn_clear_20;
	@FXML
	private TextField tf_clear_20;
	@FXML
	private Button btn_clear_24;
	@FXML
	private TextField tf_clear_24;
	
	@FXML
	private Button btn_clear_30;
	@FXML
	private TextField tf_clear_30;
	@FXML
	private Button btn_clear_34;
	@FXML
	private TextField tf_clear_34;
	
	// For systematic control of these textfields and buttons.
	private ButtonTextFieldCombo[] btfc;
	
	// The number of ButteonTextField combos on inspect & clear tap panes.
	// Each tab pane possess 4 combos.
	private final int length_btfc = 8;
	
	private final String title_alert = "訊息";
	private final String message_loginFailed = "登入失敗！";
	private final String message_troubleOccured = "發生問題，上傳失敗！";
	private final String message_noImg = "沒有照片可以上傳！";
	private final String message_exifErr = "此照片無法使用，因為日期資訊不存在！";
	private final String message_finished = "照片上傳完畢！";
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {		
		btfc = new ButtonTextFieldCombo[length_btfc];
		btfc[0] = new ButtonTextFieldCombo(btn_inspect_00, tf_inspect_00, btn_inspect_04, tf_inspect_04, true);
		btfc[1] = new ButtonTextFieldCombo(btn_inspect_10, tf_inspect_10, btn_inspect_14, tf_inspect_14, true);
		btfc[2] = new ButtonTextFieldCombo(btn_inspect_20, tf_inspect_20, btn_inspect_24, tf_inspect_24, true);
		btfc[3] = new ButtonTextFieldCombo(btn_inspect_30, tf_inspect_30, btn_inspect_34, tf_inspect_34, true);
		btfc[4] = new ButtonTextFieldCombo(btn_clear_00, tf_clear_00, btn_clear_04, tf_clear_04, false);
		btfc[5] = new ButtonTextFieldCombo(btn_clear_10, tf_clear_10, btn_clear_14, tf_clear_14, false);
		btfc[6] = new ButtonTextFieldCombo(btn_clear_20, tf_clear_20, btn_clear_24, tf_clear_24, false);
		btfc[7] = new ButtonTextFieldCombo(btn_clear_30, tf_clear_30, btn_clear_34, tf_clear_34, false);
	}
	
	private String getID() {
		return (tf_ID.getText() == null) ? "" : tf_ID.getText();
	}
	
	private String getPasswd() {
		return (tf_passwd.getText() == null) ? "" : tf_passwd.getText();
	}
	
	@FXML
	private void handleButtonGO(ActionEvent event) {
		//AutomationModel automationModel;
		List<ImageCombo> imageCombos_inspect = new ArrayList<ImageCombo>();
		List<ImageCombo> imageCombos_clear = new ArrayList<ImageCombo>();
		
		// Find out the image sets that can be uploaded.
		// Only upload the imageCombos that both dirty and clean images have set up.
		for (int i = 0; i < btfc.length; i++) {
			if (btfc[i].getImageCombo().isComplete()) {
				if (btfc[i].isForInspectJournal()) {
					imageCombos_inspect.add(btfc[i].getImageCombo());
				} else {
					imageCombos_clear.add(btfc[i].getImageCombo());
				}
				// Debug info
				System.out.println("dirty: " + btfc[i].getImageCombo().getPath_imgDirty());
				System.out.println("clean: " + btfc[i].getImageCombo().getPath_imgClean());
			}
		}
		
		// Upload the imageCombos which are not empty.
		if (!imageCombos_inspect.isEmpty() || !imageCombos_clear.isEmpty()) {
			// Show an alert for the user to re-confirm
			// whether the images to be uploaded are correct.
			String message_ready2upload
				= String.format("即將上傳：%n巡檢照片：%d\t組%n清理照片：%d\t組",
						imageCombos_inspect.size(), imageCombos_clear.size());
			if (isReadyToUpload(message_ready2upload)) {
				Runnable task = () -> {
					AutomationModel automationModel = new AutomationModel();
					try {
//						AutomationModel automationModel = new AutomationModel();
						if (automationModel.loginSuccessfully(getID(), getPasswd())) {
							// Ready to upload images
							if (!imageCombos_inspect.isEmpty()) {
								automationModel.automateJournalPost(imageCombos_inspect, "inspect");
							}
							if (!imageCombos_clear.isEmpty()) {
								automationModel.automateJournalPost(imageCombos_clear, "clear");
							}
							Platform.runLater(() -> showAlert(message_finished));
							
							// Debug info.
							System.out.println("Thread " + Thread.currentThread().getName() + " successfully finished!");
						} else {
							Platform.runLater(() -> showAlert(message_loginFailed));
						}
					} catch (NullPointerException | WebDriverException e) {
						System.out.println("Thread: " + Thread.currentThread().getName() + " is quit.");
						automationModel.quitChrome();
						Platform.runLater(() -> showAlert(message_troubleOccured));
						e.printStackTrace();
					}
				};
				Thread thread = new Thread(task);
				thread.start();
			}
		} else {
			showAlert(message_noImg);
			// Debug info
			System.out.println("no image can be uploaded.");
		} 
	}
	
	@FXML
	private void handleSelectButtons(ActionEvent event) {
		int idx = 0;
		boolean dirtyClicked = true;
		for (int i = 0; i < btfc.length; i++) {
			if (event.getSource() == btfc[i].getDirtyBtn()) {
				idx = i;
				break;
			} else if (event.getSource() == btfc[i].getCleanBtn()) {
				idx = i;
				dirtyClicked = false;
				break;
			}
		}
		
		Button clickedBtn = (dirtyClicked) ? btfc[idx].getDirtyBtn() : btfc[idx].getCleanBtn();
		TextField referencedTF = (dirtyClicked) ? btfc[idx].getDirtyTF() : btfc[idx].getCleanTF();
		
		clickedBtn.setDisable(true);
		
		/*
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
	         new ExtensionFilter("Image Files", "*.png", "*.jpg"));
		File selectedImg = fileChooser.showOpenDialog(null); */
		File selectedImg = getImage();
		if (selectedImg != null) {
			String[] info_imgValidity = getImgValidityInfo(selectedImg.getAbsolutePath(), btfc[idx].getImageCombo(), dirtyClicked);
			boolean validImg = Boolean.valueOf(info_imgValidity[0]);
			String message_alert = info_imgValidity[1];
			if (validImg) {
				setUpImgPath(idx, selectedImg.getAbsolutePath(), selectedImg.getName(), referencedTF, dirtyClicked);
			} else {
				showAlert(message_alert);
			}
			/*
			if (MetadataExtractor.exifExists(selectedImg.getAbsolutePath())) {
				ImageCombo tmp_ic = btfc[idx].getImageCombo();
				if (dirtyClicked) {
					tmp_ic.setPath_imgDirty(selectedImg.getAbsolutePath());
				} else {
					tmp_ic.setPath_imgClean(selectedImg.getAbsolutePath());
				}
				if (tmp_ic.isComplete()) {
					if (MetadataExtractor.areOnTheSameDay(tmp_ic.getPath_imgDirty(), tmp_ic.getPath_imgClean())) {
						setUpImgPath(idx, selectedImg.getAbsolutePath(), dirtyClicked);
						System.out.println("QAQ!!!");
						referencedTF.setText(selectedImg.getName());
					} else {
						String type = (dirtyClicked) ? "沒垃圾" : "有垃圾";
						String message_notSameDay = String.format("此照片無法使用，因為拍攝日期與「%s」的照片不同", type);
						showAlert(message_notSameDay);
						// for debug
						System.out.println(btfc[idx].getImageCombo().isComplete());
						System.out.println(btfc[idx].getImageCombo().getPath_imgDirty());
						System.out.println(btfc[idx].getImageCombo().getPath_imgClean());
					}
				} else {
					setUpImgPath(idx, selectedImg.getAbsolutePath(), dirtyClicked);
					referencedTF.setText(selectedImg.getName());
				}
			} else {
				showAlert(message_exifErr);
			} */
		} else {
			setUpImgPath(idx, null, null, referencedTF, dirtyClicked);
		}
		clickedBtn.setDisable(false);
	}
	
	private File getImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
	         new ExtensionFilter("Image Files", "*.png", "*.jpg"));
		File selectedImg = fileChooser.showOpenDialog(null);
		
		return selectedImg;
	}
	
	private String[] getImgValidityInfo(String path_img, ImageCombo imageCombo, boolean dirtyClicked) {
		String[] info_imgValidity = new String[2];

		// First we need to check whether the image's exif exists.
		if (!MetadataExtractor.exifExists(path_img)) {
			info_imgValidity[0] = String.valueOf(false);
			info_imgValidity[1] = message_exifErr;
			
			return info_imgValidity;
		}
		
		// Now we are sure that exif exists, but we still need to know
		// if the imageCombo is complete, whether both dirty and clean images are on the same day?
		// The EcoLife website accepts only images taken on the same day.
		ImageCombo tmp_ic = new ImageCombo(imageCombo.getPath_imgDirty(), imageCombo.getPath_imgClean());
		if (dirtyClicked) {
			tmp_ic.setPath_imgDirty(path_img);
		} else {
			tmp_ic.setPath_imgClean(path_img);
		}
		if (tmp_ic.isComplete()) {
			if (MetadataExtractor.areOnTheSameDay(tmp_ic.getPath_imgDirty(), tmp_ic.getPath_imgClean())) {
				info_imgValidity[0] = String.valueOf(true);
			} else {
				String type = (dirtyClicked) ? "沒垃圾" : "有垃圾";
				String message_notSameDay = String.format("此照片無法使用，因為拍攝日期與「%s」的照片不同", type);
				
				info_imgValidity[0] = String.valueOf(false);
				info_imgValidity[1] = message_notSameDay; 
			}
		} else {
			// Since the imageCombo is not complete, there is no need to worry about date problem.
			info_imgValidity[0] = String.valueOf(true);
		}
		
		return info_imgValidity;
	}
	
	private void setUpImgPath(int idx, String path_img, String name_img, TextField referencedTF, boolean dirtyClicked) {
		if (dirtyClicked) {
			btfc[idx].getImageCombo().setPath_imgDirty(path_img);
		} else {
			btfc[idx].getImageCombo().setPath_imgClean(path_img);
		}
		if (path_img != null) {
			referencedTF.setText(name_img);
		} else {
			referencedTF.clear();
		}
	}
	
	
/*	
	@FXML
	private void handleSelectButtons(ActionEvent event) {
		int idx = 0;
		boolean dirtyClicked = true;
		for (int i = 0; i < btfc.length; i++) {
			if (event.getSource() == btfc[i].getDirtyBtn()) {
				idx = i;
				break;
			} else if (event.getSource() == btfc[i].getCleanBtn()) {
				idx = i;
				dirtyClicked = false;
				break;
			}
		}
		
		Button clickedBtn = (dirtyClicked) ? btfc[idx].getDirtyBtn() : btfc[idx].getCleanBtn();
		TextField referencedTF = (dirtyClicked) ? btfc[idx].getDirtyTF() : btfc[idx].getCleanTF();
		
		clickedBtn.setDisable(true);
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
	         new ExtensionFilter("Image Files", "*.png", "*.jpg"));
		File selectedImg = fileChooser.showOpenDialog(null);
		if (selectedImg != null) {
			if (dirtyClicked) {
				btfc[idx].getImageCombo().setPath_imgDirty(selectedImg.getAbsolutePath());
			} else {
				btfc[idx].getImageCombo().setPath_imgClean(selectedImg.getAbsolutePath());
			}
			referencedTF.setText(selectedImg.getName());
		} else {
			if (dirtyClicked) {
				btfc[idx].getImageCombo().setPath_imgDirty(null);
			} else {
				btfc[idx].getImageCombo().setPath_imgClean(null);
			}
			referencedTF.clear();
		}
		clickedBtn.setDisable(false);
	} */
	
	private boolean isReadyToUpload(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		ButtonType btn_ok = new ButtonType("確認");
		ButtonType btn_cancel = new ButtonType("取消");
		alert.getButtonTypes().setAll(btn_ok, btn_cancel);
		alert.setTitle(title_alert);
		alert.setHeaderText(null);
		alert.setContentText(message);
		Optional<ButtonType> result = alert.showAndWait();
		
		return (result.get() == btn_ok);
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title_alert);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
