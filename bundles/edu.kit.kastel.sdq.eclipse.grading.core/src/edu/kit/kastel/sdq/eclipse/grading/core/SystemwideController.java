package edu.kit.kastel.sdq.eclipse.grading.core;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisGUIController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObservable;
import edu.kit.kastel.sdq.eclipse.grading.core.config.ConfigDao;
import edu.kit.kastel.sdq.eclipse.grading.core.config.JsonFileConfigDao;

public class SystemwideController implements ISystemwideController {

	private final Map<Integer, IAssessmentController> assessmentControllers;
	private final IArtemisGUIController artemisGUIController;

	private ConfigDao configDao;

	private Integer courseID;
	private Integer exerciseID;
	private Integer submissionID;
	private String exerciseConfigName;

	private AlertObservable alertObservable;

	public SystemwideController(final File configFile, final String exerciseConfigName, final String artemisHost, final String username, final String password) {
		this.setConfigFile(configFile);
		this.exerciseConfigName = exerciseConfigName;
		this.assessmentControllers = new HashMap<>();

		this.alertObservable = new AlertObservable();

		this.artemisGUIController = new ArtemisGUIController(this, artemisHost, username, password);
	}

	@Override
	public IAlertObservable getAlertObservable() {
		return this.alertObservable;
	}

	@Override
	public IArtemisGUIController getArtemisGUIController() {
		return this.artemisGUIController;
	}

	protected IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName) {
		Integer courseID = null;
		Integer exerciseID = null;
		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			for (IExercise exercise : course.getExercises()) {
				Optional<ISubmission> submissionOptional = exercise.getSubmissions().stream().filter(submission -> submission.getSubmissionId() == submissionID).findAny();
				if (submissionOptional.isPresent()) {
					courseID = course.getCourseId();
					exerciseID = exercise.getExerciseId();
				}
			}
		}
		if (courseID == null) {
			this.alertObservable.error("No course found with the submissionID \"" + submissionID + "\".", null);
			return null;
		}

		return this.getAssessmentController(submissionID, exerciseConfigName, courseID, exerciseID);
	}

	private IAssessmentController getAssessmentController(int submissionID, String exerciseConfigName, int courseID,
			int exerciseID) {
		if (!this.assessmentControllers.containsKey(submissionID)) {
			this.assessmentControllers.put(submissionID, new AssessmentController(this, courseID, exerciseID, submissionID, exerciseConfigName));
		}
		return this.assessmentControllers.get(submissionID);
	}

	/**
	 *
	 * @return this system's configDao.
	 */
	protected ConfigDao getConfigDao() {
		return this.configDao;
	}

	@Override
	public IAssessmentController getCurrentAssessmentController() {
		return this.getAssessmentController(this.submissionID, this.exerciseConfigName, this.courseID, this.exerciseID);
	}

	@Override
	public void onReloadAssessmentButton() {
		if (this.submissionID == null) {
			this.alertObservable.warn("Could not reload. No assessment was started, yet! (No submissionID is set)");
		}
		this.getCurrentAssessmentController().deleteEclipseProject();

		this.getArtemisGUIController().startAssessment(this.submissionID);
		this.getArtemisGUIController().downloadExerciseAndSubmission(this.courseID, this.exerciseID, this.submissionID);

		this.getCurrentAssessmentController().resetAndReload();
	}

	@Override
	public void onSaveAssessmentButton() {
		this.artemisGUIController.saveAssessment(this.submissionID, false, false);
	}

	@Override
	public boolean onStartAssessmentButton() {
		return this.startAssessment(0);
	}

	@Override
	public boolean onStartCorrectionRound1Button() {
		return this.startAssessment(0);
	}

	@Override
	public boolean onStartCorrectionRound2Button() {
		return this.startAssessment(1);
	}

	@Override
	public void onSubmitAssessmentButton() {
		this.artemisGUIController.saveAssessment(this.submissionID, true, false);

		//TODO do this only if no submitting was successful
		this.getCurrentAssessmentController().deleteEclipseProject();
		this.submissionID = null;
	}

	public void onZeroPointsForAssessment() {
		this.artemisGUIController.saveAssessment(this.submissionID, true, true);

		//TODO do this only if no submitting was successful
		this.getCurrentAssessmentController().deleteEclipseProject();
		this.submissionID = null;
	}

	@Override
	public void setConfigFile(File newConfigFile) {
		this.configDao = new JsonFileConfigDao(newConfigFile);
	}

	@Override
	public Collection<String> setCourseIdAndGetExerciseTitles(final String courseShortName) {
		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			if (course.getShortName().equals(courseShortName)) {
				this.courseID = course.getCourseId();
				return course.getExercises().stream().map(IExercise::getShortName).collect(Collectors.toList());
			}
		}
		this.alertObservable.error("No Course with the given shortName \"" + courseShortName + "\" found.", null);
		return List.of();
	}

	@Override
	public void setExerciseId(final String exerciseShortName) {
		for (ICourse course : this.getArtemisGUIController().getCourses()) {
			for (IExercise exercise : course.getExercises()) {
				if (exercise.getShortName().equals(exerciseShortName)) {
					this.exerciseID = exercise.getExerciseId();
					return;
				}
			}
		}
		this.alertObservable.error("No Exercise with the given shortName \"" + exerciseShortName + "\" found.", null);
	}

	private boolean startAssessment(int correctionRound) {
		Optional<Integer> optionalSubmissionID = this.getArtemisGUIController().startNextAssessment(this.exerciseID, correctionRound);
		if (optionalSubmissionID.isEmpty()) {
			return false;
		}
		this.submissionID = optionalSubmissionID.get();
		this.getArtemisGUIController().downloadExerciseAndSubmission(this.courseID, this.exerciseID, this.submissionID);
		return true;

	}
}
