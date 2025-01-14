package edu.kit.kastel.eclipse.grading.view.activator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.kit.kastel.sdq.eclipse.grading.api.controller.ISystemwideController;
import edu.kit.kastel.sdq.eclipse.grading.core.SystemwideController;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "plugin_gui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private ISystemwideController systemwideController;

	/**
	 * The constructor
	 */
	public Activator() {
		// NOP
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Creates a new instance of the SystemWideController
	 */
	public void createSystemWideController() {
		this.systemwideController = new SystemwideController(this.getPreferenceStore());
	}

	public ISystemwideController getSystemwideController() {
		return this.systemwideController;
	}

}
