package edu.kit.kastel.sdq.eclipse.grading.core.model.annotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.model.IAnnotation;
import edu.kit.kastel.sdq.eclipse.grading.api.model.IMistakeType;

public class DefaultAnnotationDao implements IAnnotationDao {

	private final Set<IAnnotation> annotations;

	public DefaultAnnotationDao() {
		this.annotations = new HashSet<>();
	}

	@Override
	public void addAnnotation(String annotationID, IMistakeType mistakeType, int startLine, int endLine, String fullyClassifiedClassName, String customMessage,
			Double customPenalty, int markerCharStart, int markerCharEnd) throws AnnotationException {
		if (this.idExists(annotationID)) {
			throw new AnnotationException("ID " + annotationID + " already exists!");
		}

		this.annotations.add(new Annotation(annotationID, mistakeType, startLine, endLine, fullyClassifiedClassName, customMessage, customPenalty,
				markerCharStart, markerCharEnd));
	}

	@Override
	public IAnnotation getAnnotation(String annotationId) {
		return this.annotations.stream().filter(annotation -> annotation.getUUID().equals(annotationId)).findAny().orElseThrow();
	}

	@Override
	public Set<IAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(this.annotations);
	}

	private boolean idExists(String annotationID) {
		return this.annotations.stream().anyMatch(annotation -> annotation.getUUID().equals(annotationID));
	}

	@Override
	public void modifyAnnotation(String annatationId, String customMessage, Double customPenalty) {
		final IAnnotation oldAnnotation = this.getAnnotation(annatationId);
		final IAnnotation newAnnotation = new Annotation(oldAnnotation.getUUID(), oldAnnotation.getMistakeType(), oldAnnotation.getStartLine(),
				oldAnnotation.getEndLine(), oldAnnotation.getClassFilePath(), customMessage, customPenalty, oldAnnotation.getMarkerCharStart(),
				oldAnnotation.getMarkerCharEnd());

		this.annotations.remove(oldAnnotation);
		this.annotations.add(newAnnotation);
	}

	@Override
	public void removeAnnotation(String annotationId) {
		if (this.idExists(annotationId)) {
			this.annotations.remove(this.getAnnotation(annotationId));
		}
	}

}
