package org.ifou.colorbox.javafx;
/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 */
public class WebColorFieldSkin extends InputFieldSkin {
    private InvalidationListener integerFieldValueListener;
    private boolean noChangeInValue = false;

    /**
     * Create a new IntegerFieldSkin.
     * @param control The IntegerField
     */
    public WebColorFieldSkin(final WebColorField control) {
        super(control);

        // Whenever the value changes on the control, we need to update the text
        // in the TextField. The only time this is not the case is when the update
        // to the control happened as a result of an update in the text textField.
        control.valueProperty().addListener(integerFieldValueListener = new InvalidationListener() {
            @Override public void invalidated(Observable observable) {
                updateText();
            }
        });
    }

    @Override public WebColorField getSkinnable() {
        return (WebColorField) control;
    }

    @Override public Node getNode() {
        return getTextField();
    }

    /**
     * Called by a Skinnable when the Skin is replaced on the Skinnable. This method
     * allows a Skin to implement any logic necessary to clean up itself after
     * the Skin is no longer needed. It may be used to release native resources.
     * The methods {@link #getSkinnable()} and {@link #getNode()}
     * should return null following a call to dispose. Calling dispose twice
     * has no effect.
     */
    @Override public void dispose() {
        ((WebColorField) control).valueProperty().removeListener(integerFieldValueListener);
        super.dispose();
    }

    //  "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    protected boolean accept(String text) {
        if (text.length() == 0) return true;
        if (text.matches("#[a-fA-F0-9]{0,6}") || text.matches("[a-fA-F0-9]{0,6}")) {
            return true;
        }
        return false;
    }

    protected void updateText() {
        Color color = ((WebColorField) control).getValue();
        if (color == null) color = Color.BLACK;
        getTextField().setText(getWebColor(color));
    }

    protected void updateValue() {
        if (noChangeInValue) return;
        Color value = ((WebColorField) control).getValue();
        String text = getTextField().getText() == null ? "" : getTextField().getText().trim().toUpperCase();
        if (text.matches("#[A-F0-9]{6}") || text.matches("[A-F0-9]{6}")) {
            try {
                Color newValue = (text.charAt(0) == '#')? Color.web(text) : Color.web("#"+text);
                if (!newValue.equals(value)) {
                    ((WebColorField) control).setValue(newValue);
                } else {
                    // calling setText results in updateValue - so we set this flag to true
                    // so that when this is true updateValue simply returns.
                    noChangeInValue = true; 
                    getTextField().setText(getWebColor(newValue));
                    noChangeInValue = false;
                }
            } catch (java.lang.IllegalArgumentException ex) {
                System.out.println("Failed to parse ["+text+"]");
            }
        } 
    }
    
    
    private static String getWebColor(Color color) {
        final int red = (int)(color.getRed()*255);
        final int green = (int)(color.getGreen()*255);
        final int blue = (int)(color.getBlue()*255);
        return "#" + String.format("%02X", red) +
                          String.format("%02X", green) +
                          String.format("%02X", blue);
    }
}
