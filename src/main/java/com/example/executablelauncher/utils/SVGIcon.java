package com.example.executablelauncher.utils;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class SVGIcon extends Group {
    public SVGIcon(String filePath) {
        loadSVG(filePath);
    }

    private void loadSVG(String filePath) {
        try {
            File svgFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(svgFile);

            NodeList pathNodes = document.getElementsByTagName("path");

            for (int i = 0; i < pathNodes.getLength(); i++) {
                String pathContent = pathNodes.item(i).getAttributes().getNamedItem("d").getNodeValue();

                SVGPath svgPath = new SVGPath();
                svgPath.setContent(pathContent);
                svgPath.setFill(Color.TRANSPARENT);

                if (pathNodes.item(i).getAttributes().getNamedItem("fill") != null) {
                    String fillColor = pathNodes.item(i).getAttributes().getNamedItem("fill").getNodeValue();
                    svgPath.setStroke(Color.web(fillColor));
                } else {
                    svgPath.setStroke(Color.WHITE);
                }

                this.getChildren().add(svgPath);
            }
        } catch (Exception e) {
            System.err.println("SVGIcon: SVG file could not be loaded");
        }
    }

    public void setColor(Color color) {
        for (Node child : getChildren())
            if (child instanceof SVGPath)
                ((SVGPath) child).setFill(color);
    }

    public void setStroke(Color color) {
        for (Node child : getChildren())
            if (child instanceof SVGPath)
                ((SVGPath) child).setStroke(color);
    }

    public void setScaleFactor(double scaleFactor) {
        double currentScaleX = getScaleX();
        double currentScaleY = getScaleY();

        setScaleX(currentScaleX * scaleFactor);
        setScaleY(currentScaleY * scaleFactor);
    }

    public void setDropShadow(Color shadowColor, double radius, double offsetX, double offsetY) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(shadowColor);
        dropShadow.setRadius(radius);
        dropShadow.setOffsetX(offsetX);
        dropShadow.setOffsetY(offsetY);

        for (Node child : getChildren())
            if (child instanceof SVGPath)
                child.setEffect(dropShadow);
    }
}
