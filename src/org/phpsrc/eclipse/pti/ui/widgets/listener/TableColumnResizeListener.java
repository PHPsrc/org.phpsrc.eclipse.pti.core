package org.phpsrc.eclipse.pti.ui.widgets.listener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @since 1.4.0
 */
public class TableColumnResizeListener extends ControlAdapter {
	private int columnToResize;

	public TableColumnResizeListener() {
		this(0);
	}

	public TableColumnResizeListener(int columnToResize) {
		Assert.isTrue(columnToResize >= 0);
		this.columnToResize = columnToResize;
	}

	public void controlResized(ControlEvent e) {
		if (e.widget instanceof Table) {
			Table table = ((Table) e.widget);
			TableColumn[] columns = table.getColumns();
			if (columns.length > columnToResize) {

				int width = 0;
				for (int i = 0; i < columns.length; i++) {
					if (i != columnToResize)
						width += columns[i].getWidth();
				}

				columns[columnToResize].setWidth(table.getClientArea().width - 50 - 70);
			}
		}
	}
}
