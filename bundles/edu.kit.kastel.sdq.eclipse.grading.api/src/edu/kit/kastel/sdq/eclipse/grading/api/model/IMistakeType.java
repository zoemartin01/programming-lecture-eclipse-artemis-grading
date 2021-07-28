package edu.kit.kastel.sdq.eclipse.grading.api.model;

import java.util.List;

/**
 * Represents one type of mistakes from a rating group.
 *
 */
public interface IMistakeType {

	/**
	 * Calculate penalty using only the given annotations.
	 * @param annotations
	 * @return a <i>positive</> value denoting the penalty.
	 */
	double calculatePenalty(List<IAnnotation> annotations);

	/**
	 *
	 * @return what should be shown on the button.
	 */
	String getButtonName();

	/**
	 *
	 * @return a more elaborate explanation of what the mistake is.
	 */
	String getMessage();

	/**
	 *
	 * @return the {@link IRatingGroup} this {@link IMistakeType} belongs to, which may introduce a {@link IRatingGroup#getPenaltyLimit()}!
	 */
	IRatingGroup getRatingGroup();

	String getRatingGroupName();

	/**
	 *
	 * @param annotations
	 * @return tooltip for hovering over the button. Shows rating status information based on the given annotation.
	 */
	String getTooltip(List<IAnnotation> annotations);
}