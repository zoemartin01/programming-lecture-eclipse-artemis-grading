package gui.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jface.preference.IPreferenceStore;

import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.IRatingGroup;
import edu.kit.kastel.sdq.eclipse.grading.core.AssessmentController;
import gui.activator.Activator;
import gui.preferences.PreferenceConstants;

public class AssessmentViewController {

	private static final String EXERCISE_NAME = "Final Task 2";
	private static String CONFIG_PATH;
	private IAssessmentController assessmentController;

	public AssessmentViewController() {}

	public void createAssessmentController() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		this.CONFIG_PATH = store.getString(PreferenceConstants.P_CONFIG_PATH);
		System.out.println("CONFIG" + this.CONFIG_PATH);
		this.assessmentController =  new AssessmentController(new File(AssessmentViewController.CONFIG_PATH),
				AssessmentViewController.EXERCISE_NAME);
	}
	
	public Collection<IMistakeType> getMistakeTypesForButtonView() throws IOException {
		return this.assessmentController.getMistakes();
	}
	
	public Collection<IRatingGroup> getRatingGroups() throws IOException{
		return this.assessmentController.getRatingGroups();
	}

}