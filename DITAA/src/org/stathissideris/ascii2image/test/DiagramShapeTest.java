package org.stathissideris.ascii2image.test;

import org.junit.Before;
import org.junit.Test;
import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.graphics.*;
import org.stathissideris.ascii2image.text.AbstractionGrid;
import org.stathissideris.ascii2image.text.CellSet;
import org.stathissideris.ascii2image.text.TextGrid;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class DiagramShapeTest {

	TextGrid grid = new TextGrid();
	CellSet rect = new CellSet();
	CellSet cross = new CellSet();
	CellSet L = new CellSet();
	ConversionOptions options = new ConversionOptions();
	int cellWidth = 3;
	int cellHeight = 3;
	double EPSILON = 10E-8;

	@Before public void setUp() throws IOException {
		grid.loadFrom("tests/text/art1.txt");

		for (int row = 2; row <= 10; row++) {
			for (int col = 2; col <= 14; col++) {
				if (row == 2 || row == 10 || col == 2 | col == 14) {
					rect.add(grid.new Cell(col, row));
				}
			}
		}

		cross.add(grid.new Cell(43, 17));
		cross.add(grid.new Cell(43, 18));
		cross.add(grid.new Cell(42, 18));
		cross.add(grid.new Cell(44, 18));
		cross.add(grid.new Cell(43, 19));

		for (int col = 4; col <= 18; col++) {
			L.add(grid.new Cell(col, 30));
		}
		for (int row = 30; row <= 38; row++) {
			L.add(grid.new Cell(4, row));
		}
		for (int col = 4; col <= 12; col++) {
			L.add(grid.new Cell(col, 38));
		}
		for (int col = 12; col <= 18; col++) {
			L.add(grid.new Cell(col, 34));
		}
		for (int row = 30; row <= 34; row++) {
			L.add(grid.new Cell(18, row));
		}
		for (int row = 34; row <= 38; row++) {
			L.add(grid.new Cell(12, row));
		}

	}
	
	@Test 
	public void isRectangleTest() {
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		assertTrue(rectShape.isRectangle());
		DiagramShape lShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, L, cellWidth, cellHeight);
		assertFalse(lShape.isRectangle());
	}

	@Test
	public void createArrowheadTest() {
		DiagramShape arrow = DiagramShape.createArrowhead(grid, grid.new Cell(18, 6), cellWidth, cellHeight);
		assertNotNull(arrow);
		DiagramShape notArrow = DiagramShape.createArrowhead(grid, grid.new Cell(18, 10), cellWidth, cellHeight);
		assertNull(notArrow);
	}

	@Test
	public void createSmallLineTest() {
		DiagramShape line = DiagramShape.createSmallLine(grid, grid.new Cell(17, 6), cellWidth, cellHeight);
		assertNotNull(line);
		DiagramShape notLine = DiagramShape.createSmallLine(grid, grid.new Cell(18, 6), cellWidth, cellHeight);
		assertNull(notLine);
	}

	@Test
	public void scaleTest() {
		float scale = 3;
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		double oldWidth = rectShape.getBounds().getWidth();
		rectShape.scale(scale);
		double newWidth = rectShape.getBounds().getWidth();
		assertEquals(oldWidth*scale, newWidth, EPSILON);
	}

	@Test
	public void isSmallerThanTest() {
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		DiagramShape lShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, L, cellWidth, cellHeight);
		assertTrue(rectShape.isSmallerThan(lShape));
	}

	@Test
	public void equalsTest() {
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		DiagramShape lShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, L, cellWidth, cellHeight);
		assertEquals(rectShape, rectShape);
		assertNotEquals(rectShape, lShape);
	}

	@Test
	public void getBoundsTest() {
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		assertEquals(cellWidth*12, rectShape.getBounds().getWidth(), 0);
		assertEquals(cellHeight*8, rectShape.getBounds().getHeight(), 0);
	}

	@Test
	public void getEdgesTest() {
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		DiagramShape lShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, L, cellWidth, cellHeight);
		assertEquals(rectShape.getEdges().size(), 4, 0);
		assertEquals(lShape.getEdges().size(), 6, 0);
	}

	@Test
	public void getCellEdgePointBetweenTest() {
		Diagram diagram = new Diagram(grid, options);
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect,
				diagram.getCellWidth(), diagram.getCellHeight());
		ShapePoint interiorPoint = new ShapePoint((float)rectShape.getBounds().getCenterX(),
				(float)rectShape.getBounds().getCenterY());
		ShapePoint exteriorPoint = new ShapePoint((float)diagram.getCellMaxX(diagram.getCellFor(interiorPoint)) + 100f,
				(float)rectShape.getBounds().getCenterY());

		ShapePoint result = rectShape.getCellEdgePointBetween(interiorPoint, exteriorPoint, diagram);
		assertEquals(interiorPoint.getY(), result.getY(), 0.0);
		assertEquals(diagram.getCellMaxX(diagram.getCellFor(interiorPoint)), result.getX(), 0.0);
	}

	@Test
	public void getCellEdgeProjectionPointBetweenTest() {
		Diagram diagram = new Diagram(grid, options);
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect,
				diagram.getCellWidth(), diagram.getCellHeight());
		ShapePoint interiorPoint = new ShapePoint((float)rectShape.getBounds().getCenterX(),
				(float)rectShape.getBounds().getCenterY());
		ShapePoint exteriorPoint = new ShapePoint((float)diagram.getCellMaxX(diagram.getCellFor(interiorPoint)) + 100f,
				(float)rectShape.getBounds().getCenterY());

		ShapePoint result = rectShape.getCellEdgeProjectionPointBetween(interiorPoint, exteriorPoint, diagram);
		assertEquals(interiorPoint.getY(), result.getY(), 0.0);
		assertEquals(diagram.getCellMinX(diagram.getCellFor(interiorPoint)), result.getX(), 0.0);
	}

	@Test
	public void containsTest() {
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		Rectangle smallRect = new Rectangle(10, 10, 5, 5);
		Rectangle largeRect = rectShape.getBounds();
		largeRect.width++;

		assertTrue(rectShape.contains(smallRect));
		assertFalse(rectShape.contains(largeRect));
	}

	@Test
	public void intersectsTest() {
		DiagramShape rectShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, rect, cellWidth, cellHeight);
		DiagramShape lShape = (DiagramShape) DiagramComponent.createClosedFromBoundaryCells(grid, L, cellWidth, cellHeight);
		Rectangle largeRect = rectShape.getBounds();

		assertTrue(rectShape.intersects(largeRect));
		assertFalse(rectShape.intersects(lShape.getBounds()));
	}
}
