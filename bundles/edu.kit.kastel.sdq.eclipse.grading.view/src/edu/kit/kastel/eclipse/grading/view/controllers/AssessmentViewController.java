package edu.kit.kastel.eclipse.grading.view.controllers;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.ITextSelection;

import edu.kit.kastel.eclipse.grading.view.activator.Activator;
import edu.kit.kastel.eclipse.grading.view.assessment.ArtemisGradingView;
import edu.kit.kastel.eclipse.grading.view.observers.ViewAlertObserver;
import edu.kit.kastel.eclipse.grading.view.utilities.AssessmentUtilities;
import edu.kit.kastel.sdq.eclipse.grading.api.IArtemisController;
import edu.kit.kastel.sdq.eclipse.grading.api.IAssessmentController;
import edu.kit.kastel.sdq.eclipse.grading.api.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.api.alerts.IAlertObserver;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission.Filter;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IRatingGroup;

/**
 * This class is the controller for the grading view. It creates the marker for
 * the annotations and holds all controller for the backend calls.
 *
 * @see {@link ArtemisGradingView}
 *
 */
public class AssessmentViewController {

	private IAssessmentController assessmentController;
	private IArtemisController artemisGUIController;
	private ISystemwideController systemwideController;
	private IAlertObserver alertObserver;

	public AssessmentViewController() {
		this.systemwideController = Activator.getDefault().getSystemwideController();
		this.alertObserver = new ViewAlertObserver();
		this.artemisGUIController = this.systemwideController.getArtemisGUIController();
		this.systemwideController.getAlertObservable().addAlertObserver(this.alertObserver);
		this.artemisGUIController.getAlertObservable().addAlertObserver(this.alertObserver);
	}

	/**
	 * This method creates a marker for the annotation and add a new annotation to
	 * the backlog
	 *
	 * @param mistake         (the mistake type of the new annotation)
	 * @param customMessage   (for custom mistake type, else null)
	 * @param customPenalty   (for custom mistake, else null)
	 * @param ratingGroupName (the name of the rating group of the new annotation)
	 */
	public void addAssessmentAnnotaion(IMistakeType mistake, String customMessage, Double customPenalty,
			String ratingGroupName) {

		final ITextSelection textSelection = AssessmentUtilities.getTextSelection();
		if (textSelection == null) {
			this.alertObserver.error("Text selection needed to add a new annotation", null);
			return;
		}
		final Integer startLine = textSelection.getStartLine();
		final Integer endLine = textSelection.getEndLine();
		final Integer lenght = textSelection.getLength();
		try {
			IMarker marker = AssessmentUtilities.getCurrentlyOpenFile().createMarker(AssessmentUtilities.MARKER_NAME);
			marker.setAttribute(IMarker.CHAR_START, AssessmentUtilities.getLineOffSet(startLine));
			marker.setAttribute(IMarker.CHAR_END, AssessmentUtilities.getLineOffSet(startLine) + lenght + 10);
			if (mistake != null) {
				marker.setAttribute("errorTypeDescription", mistake.getMessage());
				marker.setAttribute("errorType", mistake.getName());
			}
			marker.setAttribute("start", startLine + 1);
			marker.setAttribute("end", endLine + 1);
			marker.setAttribute("className", AssessmentUtilities.getClassNameForAnnotation());
			marker.setAttribute("ratingGroup",
					mistake == null ? ratingGroupName : mistake.getRatingGroup().getDisplayName());
			if (customMessage != null) {
				marker.setAttribute("customMessage", customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute("customPenalty", customPenalty);
			}
			if (mistake != null) {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities.createMarkerTooltip(startLine + 1, endLine + 1,
						mistake.getName(), mistake.getRatingGroup().getDisplayName(), mistake.getMessage(), null));
			} else {
				marker.setAttribute(IMarker.MESSAGE, AssessmentUtilities
						.createMarkerTooltipForCustomButton(startLine + 1, endLine + 1, customMessage, customPenalty));
			}
			this.assessmentController.addAnnotation((int) marker.getId(), mistake, startLine + 1, endLine + 1,
					AssessmentUtilities.getPathForAnnotation(), customMessage, customPenalty,
					AssessmentUtilities.getLineOffSet(startLine),
					AssessmentUtilities.getLineOffSet(startLine) + lenght + 10);
		} catch (Exception e) {
			this.alertObserver.error("Unable to create marker for annotation", e);
		}

	}

	/**
	 * creates marker for current annotations in the backend
	 */
	public void createAnnotationsMarkers() {
		this.getAnnotations().forEach(this::createMarkerForAnnotation);
	}

	private void createMarkerForAnnotation(IAnnotation annotation) {

		int startLine = annotation.getStartLine();
		int endLine = annotation.getEndLine();
		IMistakeType mistake = annotation.getMistakeType();
		String customMessage = annotation.getCustomMessage().orElse("");
		Double customPenalty = annotation.getCustomPenalty().orElse(0.0);
		try {
			IMarker marker = AssessmentUtilities
					.getFile(annotation.getClassFilePath(), this.systemwideController.getCurrentProjectName())
					.createMarker(AssessmentUtilities.MARKER_NAME);
			marker.setAttribute(IMarker.CHAR_START, annotation.getMarkerCharStart());
			marker.setAttribute(IMarker.CHAR_END, annotation.getMarkerCharEnd());
			marker.setAttribute("start", startLine + 1);
			marker.setAttribute("end", endLine + 1);
			marker.setAttribute("className", annotation.getClassFilePath());
			if (customMessage != null) {
				marker.setAttribute("customMessage", customMessage);
			}
			if (customPenalty != null) {
				marker.setAttribute("customPenalty", customPenalty);
			}
			if (mistake != null) {
				marker.setAttribute("errorTypeDescription", mistake.getMessage());
				marker.setAttribute("errorType", mistake.getName());
				marker.setAttribute("ratingGroup", mistake.getRatingGroup().getDisplayName());
				marker.setAttribute(IMarker.MESSAGE,
						AssessmentUtilities.createMarkerTooltip(startLine + 1, endLine + 1, mistake.getName(),
								mistake.getRatingGroup().getDisplayName(), mistake.getMessage(),
								annotation.getClassFilePath()));
			}
		} catch (Exception e) {
			this.alertObserver.error("Unable to create marker for given annotation:" + annotation.toString(), e);
		}
	}

	/**
	 * Deletes an annotation on the backend
	 *
	 * @param id (of the annotation)
	 */
	public void deleteAnnotation(long id) {
		this.assessmentController.removeAnnotation((int) id);
	}

	/**
	 * @return all annotations for the current assessment
	 */
	public Collection<IAnnotation> getAnnotations() {
		return this.assessmentController.getAnnotations();
	}

	/**
	 * @return all courses available at artemis
	 */
	public Collection<ICourse> getCourses() {
		return this.artemisGUIController.getCourses();
	}

	/**
	 * @return the name of all courses
	 */
	public Collection<String> getCourseShortNames() {
		return this.artemisGUIController.getCourseShortNames();
	}

	/**
	 * @param ratingGroup
	 * @return the current penalty for the given rating group
	 */
	public double getCurrentPenaltyForRatingGroup(IRatingGroup ratingGroup) {
		return this.assessmentController.calculateCurrentPenaltyForRatingGroup(ratingGroup);
	}

	/**
	 * @param courseTitle (of the selected course in the combo)
	 * @return all exams of the given course
	 */
	public Collection<String> getExamShortNames(String courseTitle) {
		return this.artemisGUIController.getExamTitles(courseTitle);
	}

	/**
	 * @param courseName (selected course in the combo)
	 * @return all exercises from the given course
	 */
	public Collection<String> getExerciseShortNames(String courseName) {
		return this.systemwideController.setCourseIdAndGetExerciseShortNames(courseName);
	}

	/**
	 * @param examShortName (of the selected exam in the combo)
	 * @return all exercises of the given exam
	 */
	public Collection<String> getExercisesShortNamesForExam(String examShortName) {
		return this.artemisGUIController.getExerciseShortNamesFromExam(examShortName);
	}

	/**
	 * @return the mistake types of the current config file
	 */
	public Collection<IMistakeType> getMistakeTypes() {
		return this.assessmentController.getMistakes();
	}

	public IRatingGroup getRatingGroupByDisplayName(String displayName) {
		return this.assessmentController.getRatingGroupByDisplayName(displayName);
	}

	/**
	 * @return the rating groups of the current config file
	 */
	public Collection<IRatingGroup> getRatingGroups() {
		return this.assessmentController.getRatingGroups();
	}

	/**
	 * @return all submissions for the given filter
	 */
	public Collection<String> getSubmissionsForBacklog() {
		return this.systemwideController.getBegunSubmissionsProjectNames(Filter.ALL);
	}

	/**
	 * @param mistakeType (of the certain button)
	 * @return tooltip for the mistake type button
	 */
	public String getToolTipForMistakeType(IMistakeType mistakeType) {
		return this.assessmentController.getTooltipForMistakeType(mistakeType);

	}

	/**
	 * Loads the selected assessment from the backlog combo
	 */
	public void onLoadAgain() {
		this.systemwideController.loadAgain();
	}

	/**
	 * reloads the current assessment and creates the marker for the given
	 * annotations
	 */
	public void onReloadAssessment() {
		this.systemwideController.reloadAssessment();
	}

	/**
	 * Saves the current assessment
	 */
	public void onSaveAssessment() {
		this.systemwideController.saveAssessment();
	}

	/**
	 * @return true, if a new assessment is started, else false
	 */
	public boolean onStartAssessment() {
		return this.systemwideController.startAssessment();
	}

	/**
	 * Starts the first correction round of an exam
	 */
	public void onStartCorrectionRound1() {
		this.systemwideController.startCorrectionRound1();
	}

	/**
	 * Starts the second correction round of an exam
	 */
	public void onStartCorrectionRound2() {
		this.systemwideController.startCorrectionRound2();
	}

	/**
	 * Submits the current assessment
	 */
	public void onSubmitAssessment() {
		this.systemwideController.submitAssessment();
	}

	/**
	 * @param projectName (of the selected assessment)
	 */
	public void setAssessedSubmission(String projectName) {
		this.systemwideController.setAssessedSubmissionByProjectName(projectName);
	}

	/**
	 * creates a new assessment controller (if needed) and adds a observer for error
	 * handling
	 */
	public void setCurrentAssessmentController() {
		this.assessmentController = this.systemwideController.getCurrentAssessmentController();
		this.assessmentController.getAlertObservable().addAlertObserver(this.alertObserver);
	}

	/**
	 * Sets the exercise ID of the selected exercise
	 *
	 * @param exerciseShortName (of the selected exercise in the combo)
	 */
	public void setExerciseID(String exerciseShortName) {
		this.systemwideController.setExerciseId(exerciseShortName);
	}

	public Set<Transition> getPossiblyTransitions() {
		return this.systemwideController.getCurrentlyPossibleTransitions();
	}

}
