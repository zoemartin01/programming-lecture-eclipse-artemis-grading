package gui.utilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class AssessmentUtilities {

	public static final String MARKER_NAME = "gui.assessment.marker";

	public static String createMarkerTooltip(int startLine, int endline, String errorTypeName, String ratingGroupName,
			String message) {
		final StringBuilder out = new StringBuilder();
		final String position = "[" + startLine + "," + endline + "]";
		out.append(position);
		out.append(",");
		out.append(AssessmentUtilities.getClassNameForAnnotation());
		out.append(",");
		out.append(ratingGroupName);
		out.append(",");
		out.append(errorTypeName);
		out.append(",");
		out.append(message);
		return out.toString();
	}

	public static String createMarkerTooltipForCustomButton(int startLine, int endline, String customMessage,
			Double customPenalty) {
		final StringBuilder builder = new StringBuilder();
		final String position = "[" + startLine + "," + endline + "]";
		builder.append(position);
		builder.append(",");
		builder.append(customMessage);
		builder.append(",");
		builder.append(customPenalty);
		builder.append(",");
		return builder.toString();
	}

	public static IFile getFile(String path) {
		System.out.println("FILE"
				+ ResourcesPlugin.getWorkspace().getRoot().getProjects()[0].getFile("assignment/" + path).toString());
		System.out.println(ResourcesPlugin.getWorkspace().getRoot().getProjects()[0].getFullPath());
		IPath test = new Path(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + path);
		System.out.println("PATH" + test);
		return ResourcesPlugin.getWorkspace().getRoot().getProjects()[0].getFile("assignment/" + path);
	}

	public static String getClassNameForAnnotation() {
		final IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();
		final String name = workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getName();
		return name;
	}

	public static IFile getCurrentFile() {
		final IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();
		final IFile file = workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
		return file;
	}

	public static Integer getLineOffSet(Integer startLine) {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow wb = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		final IWorkbenchPage activePage = wb == null ? null : window.getActivePage();

		final IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();
		Integer lineOffset = 0;
		final ITextEditor editor2 = editor.getAdapter(ITextEditor.class);
		if (editor != null) {
			final IDocumentProvider provider = editor2.getDocumentProvider();
			final IDocument document = provider.getDocument(editor2.getEditorInput());
			try {
				lineOffset = document.getLineOffset(startLine);
			} catch (final BadLocationException e) {
				e.printStackTrace();
			}
		}
		return lineOffset;
	}

	public static String getPathForAnnotation() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow wb = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		final IWorkbenchPage activePage = wb == null ? null : window.getActivePage();

		final IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();

		final IEditorInput input = editor == null ? null : editor.getEditorInput();
		final IPath path = input instanceof FileEditorInput ? ((FileEditorInput) input).getPath() : null;
		// Null Pointer Execption
		int srcIndex = 0;
		for (int i = 0; i < path.segments().length; i++) {
			if (path.segments()[i].equals("src")) {
				srcIndex = i;
				break;
			}
		}
		System.out.println(srcIndex);
		StringBuilder result = new StringBuilder().append("src");
		for (int j = srcIndex + 1; j < path.segments().length; j++) {
			result.append("/");
			result.append(path.segments()[j]);
		}
		System.out.println(result.toString());
		return result.toString();
	}

	public static ITextSelection getTextSelection() {
		final IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			final ISelection selection = editor.getSelectionProvider().getSelection();
			return (ITextSelection) selection;
		}
		return null;
	}

	public static Shell getWindowsShell() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return window.getShell();
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

}