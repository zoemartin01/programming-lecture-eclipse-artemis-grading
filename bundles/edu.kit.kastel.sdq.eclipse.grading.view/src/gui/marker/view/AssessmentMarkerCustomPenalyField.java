package gui.marker.view;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

public class AssessmentMarkerCustomPenalyField extends MarkerField {

	@Override
	public String getValue(MarkerItem item) {
		return item.getAttributeValue("customPenalty", "No custom penalty defined");
	}

}