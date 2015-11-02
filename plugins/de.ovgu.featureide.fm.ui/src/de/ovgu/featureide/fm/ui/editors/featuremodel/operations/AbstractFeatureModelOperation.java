/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.ui.editors.featuremodel.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.event.FeatureModelEvent;
import de.ovgu.featureide.fm.core.base.event.PropertyConstants;

/**
 * This operation should be used as superclass for all operations on the feature model.
 * It provides standard handling and refreshing of the model.
 * 
 * @author Jens Meinicke
 * @author Sebastian Krieter
 */
public abstract class AbstractFeatureModelOperation extends AbstractOperation {

	protected final IFeatureModel featureModel;

	private String eventId = PropertyConstants.MODEL_DATA_CHANGED;
	private Object editor = null;

	public AbstractFeatureModelOperation(IFeatureModel featureModel, String label) {
		super(label);
		this.featureModel = featureModel;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		redo();
		fireEvent();
		return Status.OK_STATUS;
	}

	protected abstract void redo();

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		undo();
		fireEvent();
		return Status.OK_STATUS;
	}

	protected abstract void undo();

	private void fireEvent() {
		featureModel.fireEvent(new FeatureModelEvent(featureModel, editor, false, eventId, null, null));
	}

	protected String getEventId() {
		return eventId;
	}

	protected void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Object getEditor() {
		return editor;
	}

	public void setEditor(Object editor) {
		this.editor = editor;
	}

}
