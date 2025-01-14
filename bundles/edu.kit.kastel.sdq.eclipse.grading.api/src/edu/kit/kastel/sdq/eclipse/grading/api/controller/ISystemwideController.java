package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.io.File;
import java.util.List;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.SubmissionFilter;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.State;
import edu.kit.kastel.sdq.eclipse.grading.api.backendstate.Transition;

public interface ISystemwideController extends IController {

	/**
	 *
	 * @return the one artemis gui controller.
	 */
	IArtemisController getArtemisGUIController();

	/**
	 * <B>BACKLOG</B><br/>
	 * Get all submissions (their project names) which were sometime started by the
	 * calling tutor. Based on current exercise.
	 * ISystemwideController::setExerciseId() must have been called before! TODO see
	 * tolle Zustandsautomat-Grafik!
	 *
	 * @param filter determine which kinds of submissions should be filtered (= be
	 *               in the result)
	 * @return the respective project Names (unique).
	 */
	List<String> getBegunSubmissionsProjectNames(SubmissionFilter filter);

	/**
	 *
	 * Get assessment controller for current state (courseID, exerciseID,
	 * submissionID, exerciseConfig).
	 */
	IAssessmentController getCurrentAssessmentController();

	/**
	 *
	 * @return the possible transitions, based on the current {@link State}
	 */
	Set<Transition> getCurrentlyPossibleTransitions();

	/**
	 * Pre-Condition: course, exercise and submission must be set!
	 *
	 * @return the current project name.
	 */
	String getCurrentProjectName();

	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Loads an already assessed (started, saved or even submitted) submission
	 * for re-assessment.
	 *
	 * <li>You need to select a submission via {@link #setAssessedSubmission(int)},
	 * first! Has the same effect as {@link #startAssessment()} otherwise.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void loadAgain();

	/**
	 * TODO write doc
	 */
	void onZeroPointsForAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Deletes local project. Renews the lock and downloads the submission
	 * project again.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void reloadAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Saves the assessment to Artemis.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void saveAssessment();

	/**
	 * <B>BACKLOG</B><br/>
	 * <li>Only sets the submission, does not start the assessment!.
	 * <li>You want to have called {@link #getBegunSubmissions(ISubmission.Filter)},
	 * first!
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 */
	void setAssessedSubmissionByProjectName(String projectName);

	/**
	 * set the new annotation model config globally.
	 *
	 * @param newConfigFile
	 */
	void setConfigFile(File newConfigFile);

	/**
	 *
	 * <B>ASSESSMENT - STATE</B><br/>
	 * <li>Set the current course for further assessment-related actions, such as
	 * {@link #setExerciseId(String)}
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @param courseShortName unique short name
	 * @return all exercise short names. Can be used to call
	 *         {@link #setExerciseId(String)}.
	 * @throws ArtemisClientException
	 */
	List<String> setCourseIdAndGetExerciseShortNames(String courseShortName) throws ArtemisClientException;

	/**
	 * <B>ASSESSMENT - STATE</B><br/>
	 * <li>Set the current exercise for further assessment-related actions, such as
	 * {@link #startAssessment()}
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @param exerciseShortName unique short name
	 * @throws ArtemisClientException
	 */
	void setExerciseId(String exerciseShortName) throws ArtemisClientException;

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>current state (exerciseID, courseID) is used for call to artemis:
	 * nextAssessement
	 * <li>if an assessment is available, it is downloaded and locked.
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @return whether a new assessment was started or not, depending on whether
	 *         there was a submission available.
	 */
	boolean startAssessment();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>The same as {@link #startAssessment()}.
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @return whether a new assessment was started or not, depending on whether
	 *         there was a submission available.
	 */
	boolean startCorrectionRound1();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Like {@link #startAssessment()}, but with correction round 2 as a
	 * parameter.
	 * <li>See docs/Zustandshaltung-Automat
	 *
	 * @return whether a new assessment was started or not, depending on whether
	 *         there was a submission available.
	 */
	boolean startCorrectionRound2();

	/**
	 * <B>ASSESSMENT</B><br/>
	 * <li>Saves and submits the assessment to Artemis. Deletes project (in eclipse
	 * and on files system) thereafter.
	 * <li>See docs/Zustandshaltung-Automat
	 */
	void submitAssessment();
}
