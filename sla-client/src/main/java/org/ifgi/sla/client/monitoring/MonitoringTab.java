package org.ifgi.sla.client.monitoring;

import java.util.ArrayList;

import org.ifgi.sla.shared.Measurement;
import org.ifgi.sla.shared.Pair;

import ua.metallic.ofcchart.client.ChartWidget;
import ua.metallic.ofcchart.client.model.ChartData;
import ua.metallic.ofcchart.client.model.axis.XAxis;
import ua.metallic.ofcchart.client.model.axis.YAxis;
import ua.metallic.ofcchart.client.model.elements.LineChart;

import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ItemClickEvent;
import com.smartgwt.client.widgets.menu.events.ItemClickHandler;
import com.smartgwt.client.widgets.tab.Tab;

public class MonitoringTab extends Tab {
	private VLayout layout;
	
	private ChartWidget chart = null;
	private ChartData cd = null;
	private Menu menu = null;
	private ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
	IMenuButton menuButton = null;

	private ListGrid listGrid = null;
	
	/**
	 * MonitoringTab shows the measurements of the chosen agreement. If the measured value is a number
	 * in addition to the listgrid a line chart is shown with the average values. 
	 * @author Kristof Lange
	 * @version 1.0
	 */


	public MonitoringTab(String title) {
		super(title);

		layout = new VLayout();
		this.setPane(layout);

		initializeMenu();
		addTree();

	}

	private void initializeMenu() {
		menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(10);
		menuButton = new IMenuButton("Properties", menu);
		menuButton.setWidth(100);
		menuButton.setDisabled(true);
		layout.addMember(menuButton);
		chart = new ChartWidget();
		cd = new ChartData();
		chart.setChartData(cd);
		chart.setHeight("70%");
		chart.setVisibility(Visibility.HIDDEN);
		layout.addMember(chart);
	}

	private void addTree() {
		ListGridField propField = new ListGridField("Property");
		ListGridField timeField = new ListGridField("Timestamp");
		ListGridField valueField = new ListGridField("Value");
		
		listGrid = new ListGrid();
		listGrid.setWidth100();
		listGrid.setDataPageSize(10);
		listGrid.setFields(propField, timeField, valueField);
		listGrid.draw();
		layout.addMember(listGrid);
	}

	public void updateMeasurement(ArrayList<Measurement> measurements) {
		setMenuItemsHandler(measurements);
		records.clear();
		ListGridRecord record;
		int id = 0;
		for (Measurement measurement : measurements) {

			for (Pair pair : measurement.getPropertyAttributes()) {
				record = new ListGridRecord();
				record.setAttribute("ID", id);
				record.setAttribute("Timestamp", measurement.getTimeStamp());
				record.setAttribute("Property", pair.getKey());
				record.setAttribute("Value", pair.getValue());
				id++;

				records.add(record);
			}

		}
		listGrid.setData(records.toArray(new ListGridRecord[records.size()]));
		listGrid.markForRedraw();
	}

	private void setMenuItemsHandler(final ArrayList<Measurement> measurements) {

		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
		for (Pair pair : measurements.get(0).getPropertyAttributes()) {
			MenuItem item = new MenuItem(pair.getKey());
			items.add(item);
		}
		menu.addItemClickHandler(new ItemClickHandler() {

			public void onItemClick(ItemClickEvent event) {
				if (measurements.size() > 20) {
					chart.setVisibility(Visibility.HIDDEN);
					getLineChartData(measurements, event);
				} else
					chart.setVisibility(Visibility.HIDDEN);

				ArrayList<ListGridRecord> recordset = new ArrayList<ListGridRecord>();

				for (ListGridRecord rec : records) {

					if (rec.getAttribute("Property").equals(
							event.getItem().getTitle()))
						recordset.add(rec);
				}

				listGrid.setData(recordset.toArray(new ListGridRecord[recordset
						.size()]));

			}
		});

		menu.setItems(items.toArray(new MenuItem[items.size()]));

		menuButton.markForRedraw();
		menuButton.setDisabled(false);

	}

	private ChartData getLineChartData(ArrayList<Measurement> measurements,
			ItemClickEvent event) {
		chart.clear();
		cd = new ChartData(event.getItem().getTitle() + "(average)",
				"font-size: 11px; font-family: Verdana; text-align: center;");
		cd.setBackgroundColour("#ffffff");
		LineChart lc1 = new LineChart();

		lc1.setColour("#ff0000");
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Number> values = new ArrayList<Number>();
		double val = 0;
		int hideID = -1;
		for (int j = 0; j < measurements.size(); j++) {
			for (int i = 0; i < measurements.get(j).getPropertyAttributes()
					.size(); i++) {
				if (event.getItem().getTitle().equals(
						measurements.get(j).getPropertyAttributes().get(i)
								.getKey())) {
					hideID = i;
					try {
						val += Double.valueOf(measurements.get(j)
								.getPropertyAttributes().get(i).getValue());
					} catch (NumberFormatException e) {
						chart.clear();
						return cd;
					}
					lc1.setText(measurements.get(j).getPropertyAttributes()
							.get(i).getKey()
							+ "(average)");

				}
			}
			if ((j % ((int) (Math.floor(measurements.size() / 20)))) == 0) {
				labels.add(measurements.get(j).getTimeStamp());
				values.add(Math.floor(val / (measurements.size() / 20)));
				val = 0;
			}

		}

		XAxis xa = new XAxis();
		xa.setLabels(labels);
		xa.setOffset(true);

		xa.getLabels().setRotationAngle(70);

		cd.setXAxis(xa);

		YAxis ya = new YAxis();
		double min = 100000000;
		double max = -1;
		for (Number value : values) {
			if (value.doubleValue() >= max)
				max = value.doubleValue();
			if (value.doubleValue() <= min)
				min = value.doubleValue();
		}
		if (max == 0)
			max += 1;

		ya.setRange(min, max/* Math.max(1, Math.min(max, 5000)) */, 10);

		lc1.addValues(values);
		cd.addElements(lc1);

		cd.setYAxis(ya);

		chart.setChartData(cd);
		if (Double.valueOf(Math.floor(Double.valueOf(measurements.get(0)
				.getPropertyAttributes().get(hideID).getValue()))) instanceof Double)

			chart.setVisibility(Visibility.INHERIT);
		else
			chart.setVisibility(Visibility.HIDDEN);

		return cd;
	}

	public ChartWidget getChart() {
		return chart;
	}

}