package org.ifou.colorbox;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.ifou.colorbox.javafx.IntegerField;
import org.ifou.colorbox.javafx.IntegerFieldSkin;
import org.ifou.colorbox.javafx.WebColorField;
import org.ifou.colorbox.javafx.WebColorFieldSkin;
import org.ifou.colorbox.mousemove.MouseMoveChecker;
import org.ifou.colorbox.mousemove.MouseMoveEvent;
import org.ifou.colorbox.mousemove.MouseMoveListener;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;


public class ColorBox extends HBox {

	private ColorRectPane colorRectPane;
	private ControlsPane controlsPane;
	private ColorChecker colorChecker;

	private ObjectProperty<Color> currentColorProperty = new SimpleObjectProperty<>(Color.WHITE);
	private ObjectProperty<Color> customColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);

	private WebColorField webField = null;
	private Stage stage;

	public ColorBox(Stage st) {
		stage = st;
		getStyleClass().add("custom-color-dialog");
		colorRectPane = new ColorRectPane();
		controlsPane = new ControlsPane();
		colorChecker = new ColorChecker();
		setHgrow(controlsPane, Priority.ALWAYS);
		getChildren().addAll(colorRectPane, controlsPane, colorChecker);
	}

	public void setCurrentColor(Color currentColor) {
		this.currentColorProperty.set(currentColor);
	}

	Color getCurrentColor() {
		return currentColorProperty.get();
	}

	ObjectProperty<Color> customColorProperty() {
		return customColorProperty;
	}

	public void setCustomColor(Color color) {
		customColorProperty.set(color);
	}

	Color getCustomColor() {
		return customColorProperty.get();
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();
	}

	/*
	 * ------------------------------------------------------------------------
	 */

	private class ColorRectPane extends HBox {

		private Pane colorRect;
		private Pane colorBar;
		private Pane colorRectOverlayOne;
		private Pane colorRectOverlayTwo;
		private Region colorRectIndicator;
		private Region colorBarIndicator;

		private boolean changeIsLocal = false;
		private DoubleProperty hue = new SimpleDoubleProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateHSBColor();
					changeIsLocal = false;
				}
			}
		};
		private DoubleProperty sat = new SimpleDoubleProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateHSBColor();
					changeIsLocal = false;
				}
			}
		};
		private DoubleProperty bright = new SimpleDoubleProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateHSBColor();
					changeIsLocal = false;
				}
			}
		};
		private IntegerProperty red = new SimpleIntegerProperty(0) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateRGBColor();
					changeIsLocal = false;
				}
			}
		};

		private IntegerProperty green = new SimpleIntegerProperty(0) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateRGBColor();
					changeIsLocal = false;
				}
			}
		};

		private IntegerProperty blue = new SimpleIntegerProperty(0) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateRGBColor();
					changeIsLocal = false;
				}
			}
		};

		private DoubleProperty alpha = new SimpleDoubleProperty(100) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					setCustomColor(new Color(getCustomColor().getRed(), getCustomColor().getGreen(),
							getCustomColor().getBlue(), clamp(alpha.get() / 100)));
					changeIsLocal = false;
				}
			}
		};

		private void updateRGBColor() {
			Color newColor = Color.rgb(red.get(), green.get(), blue.get(), clamp(alpha.get() / 100));
			hue.set(newColor.getHue());
			sat.set(newColor.getSaturation() * 100);
			bright.set(newColor.getBrightness() * 100);
			setCustomColor(newColor);
		}

		private void updateHSBColor() {
			Color newColor = Color.hsb(hue.get(), clamp(sat.get() / 100), clamp(bright.get() / 100),
					clamp(alpha.get() / 100));
			red.set(doubleToInt(newColor.getRed()));
			green.set(doubleToInt(newColor.getGreen()));
			blue.set(doubleToInt(newColor.getBlue()));
			setCustomColor(newColor);
		}

		private void colorChanged() {
			if (!changeIsLocal) {
				changeIsLocal = true;
				hue.set(getCustomColor().getHue());
				sat.set(getCustomColor().getSaturation() * 100);
				bright.set(getCustomColor().getBrightness() * 100);
				red.set(doubleToInt(getCustomColor().getRed()));
				green.set(doubleToInt(getCustomColor().getGreen()));
				blue.set(doubleToInt(getCustomColor().getBlue()));
				changeIsLocal = false;
			}
		}

		public ColorRectPane() {

			getStyleClass().add("color-rect-pane");

			customColorProperty().addListener(new ChangeListener<Color>() {

				@Override
				public void changed(ObservableValue<? extends Color> ov, Color t, Color t1) {
					colorChanged();
				}
			});

			colorRectIndicator = new Region();
			colorRectIndicator.setId("color-rect-indicator");
			colorRectIndicator.setManaged(false);
			colorRectIndicator.setMouseTransparent(true);
			colorRectIndicator.setCache(true);

			final Pane colorRectOpacityContainer = new StackPane();

			colorRect = new StackPane() {
				// This is an implementation of square control that chooses its
				// size to fill the available height
				@Override
				public Orientation getContentBias() {
					return Orientation.VERTICAL;
				}

				@Override
				protected double computePrefWidth(double height) {
					return height;
				}

				@Override
				protected double computeMaxWidth(double height) {
					return height;
				}
			};
			colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

			Pane colorRectHue = new Pane();
			colorRectHue.backgroundProperty().bind(new ObjectBinding<Background>() {

				{
					bind(hue);
				}

				@Override
				protected Background computeValue() {
					return new Background(
							new BackgroundFill(Color.hsb(hue.getValue(), 1.0, 1.0), CornerRadii.EMPTY, Insets.EMPTY));
				}
			});

			colorRectOverlayOne = new Pane();
			colorRectOverlayOne.getStyleClass().add("color-rect");
			colorRectOverlayOne
					.setBackground(new Background(new BackgroundFill(
							new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
									new Stop(0, Color.rgb(255, 255, 255, 1)), new Stop(1, Color.rgb(255, 255, 255, 0))),
							CornerRadii.EMPTY, Insets.EMPTY)));

			EventHandler<MouseEvent> rectMouseHandler = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					final double x = event.getX();
					final double y = event.getY();
					sat.set(clamp(x / colorRect.getWidth()) * 100);
					bright.set(100 - (clamp(y / colorRect.getHeight()) * 100));
				}
			};

			colorRectOverlayTwo = new Pane();
			colorRectOverlayTwo.getStyleClass().addAll("color-rect");
			colorRectOverlayTwo
					.setBackground(new Background(new BackgroundFill(
							new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
									new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
							CornerRadii.EMPTY, Insets.EMPTY)));
			colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
			colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

			Pane colorRectBlackBorder = new Pane();
			colorRectBlackBorder.setMouseTransparent(true);
			colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

			colorBar = new Pane();
			colorBar.getStyleClass().add("color-bar");
			colorBar.setBackground(
					new Background(new BackgroundFill(createHueGradient(), CornerRadii.EMPTY, Insets.EMPTY)));

			colorBarIndicator = new Region();
			colorBarIndicator.setId("color-bar-indicator");
			colorBarIndicator.setMouseTransparent(true);
			colorBarIndicator.setCache(true);

			colorRectIndicator.layoutXProperty().bind(sat.divide(100).multiply(colorRect.widthProperty()));
			colorRectIndicator.layoutYProperty()
					.bind(Bindings.subtract(1, bright.divide(100)).multiply(colorRect.heightProperty()));
			colorBarIndicator.layoutYProperty().bind(hue.divide(360).multiply(colorBar.heightProperty()));
			colorRectOpacityContainer.opacityProperty().bind(alpha.divide(100));

			EventHandler<MouseEvent> barMouseHandler = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					final double y = event.getY();
					hue.set(clamp(y / colorRect.getHeight()) * 360);
				}
			};

			colorBar.setOnMouseDragged(barMouseHandler);
			colorBar.setOnMousePressed(barMouseHandler);

			colorBar.getChildren().setAll(colorBarIndicator);
			colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
			colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
			HBox.setHgrow(colorRect, Priority.SOMETIMES);
			getChildren().addAll(colorRect, colorBar);
		}

		private void updateValues() {
			if (getCurrentColor() == null) {
				setCurrentColor(Color.TRANSPARENT);
			}
			changeIsLocal = true;
			// Initialize hue, sat, bright, color, red, green and blue
			hue.set(getCurrentColor().getHue());
			sat.set(getCurrentColor().getSaturation() * 100);
			bright.set(getCurrentColor().getBrightness() * 100);
			alpha.set(getCurrentColor().getOpacity() * 100);
			setCustomColor(
					Color.hsb(hue.get(), clamp(sat.get() / 100), clamp(bright.get() / 100), clamp(alpha.get() / 100)));
			red.set(doubleToInt(getCustomColor().getRed()));
			green.set(doubleToInt(getCustomColor().getGreen()));
			blue.set(doubleToInt(getCustomColor().getBlue()));
			changeIsLocal = false;
		}

		@Override
		protected void layoutChildren() {
			super.layoutChildren();

			// to maintain default size
			colorRectIndicator.autosize();
			// to maintain square size
			double size = Math.min(colorRect.getWidth(), colorRect.getHeight());
			colorRect.resize(size, size);
			colorBar.resize(colorBar.getWidth(), size);
		}
	}

	/*
	 * ------------------------------------------------------------------------
	 */

	private class ControlsPane extends VBox {

		private ToggleButton hsbButton;
		private ToggleButton rgbButton;
		private ToggleButton webButton;
		private HBox hBox;

		private Label labels[] = new Label[4];
		private Slider sliders[] = new Slider[4];
		private IntegerField fields[] = new IntegerField[4];
		private Label units[] = new Label[4];
		private Region whiteBox;

		private GridPane settingsPane = new GridPane();

		public ControlsPane() {
			getStyleClass().add("controls-pane");

			// currentTransparent = new Region();
			// currentTransparent.getStyleClass().addAll("transparent-pattern");

			// newColorRect = new Region();
			// newColorRect.getStyleClass().add("color-rect");
			// newColorRect.setId("new-color");
			// newColorRect.backgroundProperty().bind(new
			// ObjectBinding<Background>() {
			// {
			// bind(customColorProperty);
			// }
			// @Override protected Background computeValue() {
			// return new Background(new
			// BackgroundFill(customColorProperty.get(), CornerRadii.EMPTY,
			// Insets.EMPTY));
			// }
			// });

			whiteBox = new Region();
			whiteBox.getStyleClass().add("customcolor-controls-background");

			hsbButton = new ToggleButton("HSB");
			hsbButton.getStyleClass().add("left-pill");
			rgbButton = new ToggleButton("RGB");
			rgbButton.getStyleClass().add("center-pill");
			webButton = new ToggleButton("Web");
			webButton.getStyleClass().add("right-pill");
			final ToggleGroup group = new ToggleGroup();

			hBox = new HBox();
			hBox.setAlignment(Pos.CENTER);
			hBox.getChildren().addAll(hsbButton, rgbButton, webButton);

			Region spacer1 = new Region();
			spacer1.setId("spacer1");
			Region leftSpacer = new Region();
			leftSpacer.setId("spacer-side");
			Region rightSpacer = new Region();
			rightSpacer.setId("spacer-side");
			Region bottomSpacer = new Region();
			bottomSpacer.setId("spacer-bottom");

			settingsPane = new GridPane();
			settingsPane.setId("settings-pane");
			settingsPane.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(),
					new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints());
			settingsPane.getColumnConstraints().get(0).setHgrow(Priority.NEVER);
			settingsPane.getColumnConstraints().get(2).setHgrow(Priority.ALWAYS);
			settingsPane.getColumnConstraints().get(3).setHgrow(Priority.NEVER);
			settingsPane.getColumnConstraints().get(4).setHgrow(Priority.NEVER);
			settingsPane.getColumnConstraints().get(5).setHgrow(Priority.NEVER);
			settingsPane.add(whiteBox, 0, 0, 6, 5);
			settingsPane.add(hBox, 0, 0, 6, 1);
			settingsPane.add(leftSpacer, 0, 0);
			settingsPane.add(rightSpacer, 5, 0);
			settingsPane.add(bottomSpacer, 0, 4);

			webField = new WebColorField();
			webField.getStyleClass().add("web-field");
			webField.setSkin(new WebColorFieldSkin(webField));
			webField.valueProperty().bindBidirectional(customColorProperty);
			webField.visibleProperty().bind(group.selectedToggleProperty().isEqualTo(webButton));
			settingsPane.add(webField, 2, 1);

			// Color settings Grid Pane
			for (int i = 0; i < 4; i++) {
				labels[i] = new Label();
				labels[i].getStyleClass().add("settings-label");

				sliders[i] = new Slider();

				fields[i] = new IntegerField();
				fields[i].getStyleClass().add("color-input-field");
				fields[i].setSkin(new IntegerFieldSkin(fields[i]));

				units[i] = new Label(i == 0 ? "\u00B0" : "%");
				units[i].getStyleClass().add("settings-unit");

				if (i > 0 && i < 3) {
					// first row and opacity labels are always visible
					// second and third row labels are not visible in Web page
					labels[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
				}
				if (i < 3) {
					// sliders and fields shouldn't be visible in Web page
					sliders[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
					fields[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
					units[i].visibleProperty().bind(group.selectedToggleProperty().isEqualTo(hsbButton));
				}
				int row = 1 + i;
				if (i == 3) {
					// opacity row is shifted one gridPane row down
					row++;
				}

				settingsPane.add(labels[i], 1, row);
				settingsPane.add(sliders[i], 2, row);
				settingsPane.add(fields[i], 3, row);
				settingsPane.add(units[i], 4, row);
			}

			set(3, "Opacity:", 100, colorRectPane.alpha);

			hsbButton.setToggleGroup(group);
			rgbButton.setToggleGroup(group);
			webButton.setToggleGroup(group);
			group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

				@Override
				public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
					if (newValue == null) {
						group.selectToggle(oldValue);
					} else {
						if (newValue == hsbButton) {
							showHSBSettings();
						} else if (newValue == rgbButton) {
							showRGBSettings();
						} else {
							showWebSettings();
						}
					}
				}
			});
			group.selectToggle(hsbButton);

			getChildren().addAll(new Pane(), settingsPane);
		}

		private void showHSBSettings() {
			set(0, "Hue:", 360, colorRectPane.hue);
			set(1, "Saturation:", 100, colorRectPane.sat);
			set(2, "Brightness:", 100, colorRectPane.bright);
		}

		private void showRGBSettings() {
			set(0, "Red:", 255, colorRectPane.red);
			set(1, "Green:", 255, colorRectPane.green);
			set(2, "Blue:", 255, colorRectPane.blue);
		}

		private void showWebSettings() {
			labels[0].setText("Web:");
		}

		private Property<Number>[] bindedProperties = new Property[4];

		private void set(int row, String caption, int maxValue, Property<Number> prop) {
			labels[row].setText(caption);
			if (bindedProperties[row] != null) {
				sliders[row].valueProperty().unbindBidirectional(bindedProperties[row]);
				fields[row].valueProperty().unbindBidirectional(bindedProperties[row]);
			}
			sliders[row].setMax(maxValue);
			sliders[row].valueProperty().bindBidirectional(prop);
			fields[row].setMaxValue(maxValue);
			fields[row].valueProperty().bindBidirectional(prop);
			bindedProperties[row] = prop;
		}
	}

	private class ColorChecker extends VBox implements MouseMoveListener {
		private Group group;
		private Group group2;
		private Canvas canvas;
		private ImageView view;
		private Region colorRect;
		private Region currentTransparent;
		private Region border;
		private Button buttonchecker;
		private Robot robot;

		private ChangeListener<Boolean> focuschange = new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				stopAction();
			}
		};

		private EventHandler<Event> eventMouse = new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				stopAction();
			}
		};

		public ColorChecker() {

			setSpacing(25);
			setAlignment(Pos.CENTER);

			try {
				robot = new Robot();
			} catch (AWTException e1) {
				e1.printStackTrace();
			}

			group = new Group();
			group2 = new Group();

			canvas = new Canvas(90, 90);
			canvas.setOpacity(1);
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setLineWidth(1.5);
			gc.setStroke(Color.BLACK);
			gc.strokeRect(40, 40, 10, 10);
			gc.setStroke(Color.WHITE);
			gc.strokeRect(40.75, 40.75, 8.5, 8.5);
			gc.setLineWidth(3);
			gc.setStroke(Color.BLACK);
			gc.strokeRect(0, 0, 90, 90);

			view = new ImageView();
			view.setPreserveRatio(false);
			view.setSmooth(false);
			view.fitWidthProperty().bind(canvas.widthProperty());
			view.fitHeightProperty().bind(canvas.heightProperty());

			colorRect = new Region();
			colorRect.getStyleClass().add("color-rect");
			colorRect.setId("new-color");
			colorRect.setMinSize(90, 90);
			colorRect.setMaxSize(90, 90);
			colorRect.backgroundProperty().bind(new ObjectBinding<Background>() {
				{
					bind(customColorProperty);
				}

				@Override
				protected Background computeValue() {
					return new Background(
							new BackgroundFill(customColorProperty.get(), CornerRadii.EMPTY, Insets.EMPTY));
				}
			});

			currentTransparent = new Region();
			currentTransparent.getStyleClass().addAll("transparent-pattern");
			currentTransparent.setMinSize(90, 90);
			currentTransparent.setMaxSize(90, 90);

			group2.getChildren().addAll(view, canvas);
			group2.setVisible(false);

			border = new Region();
			border.setMinSize(90, 90);
			border.setMaxSize(90, 90);
			border.setStyle("-fx-border-color: #cfcfcf;-fx-border-width:3px;");

			group.getChildren().addAll(currentTransparent, colorRect, group2, border);
			getChildren().add(group);

			buttonchecker = new Button();
			buttonchecker.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("images/pipet.png"))));
			buttonchecker.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							MouseMoveChecker.getInstance().addMouseMoveListener(ColorChecker.this);
							group2.setVisible(true);
							buttonchecker.setDisable(true);
							stage.focusedProperty().addListener(focuschange);
							stage.addEventFilter(MouseEvent.MOUSE_CLICKED, eventMouse);

						}
					});
				}
			});

			getChildren().add(buttonchecker);
		}

		private void stopAction() {
			MouseMoveChecker.getInstance().removeMouseMoveListener(ColorChecker.this);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					group2.setVisible(false);
					buttonchecker.setDisable(false);
					stage.removeEventFilter(MouseEvent.MOUSE_CLICKED, eventMouse);
					stage.focusedProperty().removeListener(focuschange);
				}
			});
		}

		@Override
		public void mouseMoved(MouseMoveEvent event) {
			final Point point = event.getPoint();
			BufferedImage im = robot.createScreenCapture(new Rectangle((int) point.getX() - 4, (int) point.getY() - 4, 9, 9));
			view.setImage(SwingFXUtils.toFXImage(scale(im, 10), null));
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					java.awt.Color color = robot.getPixelColor(point.x, point.y);
					setCustomColor(Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
				}
			});

		}

		public BufferedImage scale(BufferedImage bi, double scaleValue) {
			int width = (int) (bi.getWidth() * scaleValue);
			int height = (int) (bi.getHeight() * scaleValue);
			BufferedImage biNew = new BufferedImage(width, height, bi.getType());
			Graphics2D graphics = biNew.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			graphics.drawImage(bi, 0, 0, width, height, null);
			graphics.dispose();
			return biNew;
		}

	}

	static double clamp(double value) {
		return value < 0 ? 0 : value > 1 ? 1 : value;
	}

	private static LinearGradient createHueGradient() {
		double offset;
		Stop[] stops = new Stop[255];
		for (int y = 0; y < 255; y++) {
			offset = (double) (1 - (1.0 / 255) * y);
			int h = (int) ((y / 255.0) * 360);
			stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
		}
		return new LinearGradient(0f, 1f, 0f, 0f, true, CycleMethod.NO_CYCLE, stops);
	}

	private static int doubleToInt(double value) {
		return (int) (value * 255 + 0.5); // Adding 0.5 for rounding only
	}
}
