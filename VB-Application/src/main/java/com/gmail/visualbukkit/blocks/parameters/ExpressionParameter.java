package com.gmail.visualbukkit.blocks.parameters;

import com.gmail.visualbukkit.VisualBukkitApp;
import com.gmail.visualbukkit.blocks.*;
import com.gmail.visualbukkit.project.BuildContext;
import com.gmail.visualbukkit.ui.*;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import org.controlsfx.control.PopOver;
import org.json.JSONObject;

import java.util.List;

public class ExpressionParameter extends BlockParameter<StyleableVBox> {

    private static final PseudoClass CONNECTING_STYLE_CLASS = PseudoClass.getPseudoClass("connecting");
    private static final PseudoClass DRAG_OVER_STYLE_CLASS = PseudoClass.getPseudoClass("drag-over");

    private ClassInfo type;
    private Expression.Block expression;
    private Button placeholder;

    public ExpressionParameter(String label, ClassInfo type) {
        super(label, new StyleableVBox());
        this.type = type;

        placeholder = new Button("<" + type.getDisplayClassName() + ">");
        placeholder.getStyleClass().add("expression-parameter");
        control.getChildren().add(placeholder);

        placeholder.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                ExpressionSelector.show(this, 2 * e.getSceneY() > VisualBukkitApp.getScene().getHeight() ?
                        PopOver.ArrowLocation.BOTTOM_LEFT :
                        PopOver.ArrowLocation.TOP_LEFT);
            }
        });

        placeholder.setOnDragOver(e -> {
            Object source = e.getGestureSource();
            if (source instanceof ExpressionSource || source instanceof Expression.Block) {
                placeholder.pseudoClassStateChanged(DRAG_OVER_STYLE_CLASS, true);
                e.acceptTransferModes(TransferMode.ANY);
                e.consume();
            }
        });

        placeholder.setOnDragDropped(e -> {
            Object source = e.getGestureSource();
            if (source instanceof ExpressionSource || source instanceof Expression.Block) {
                UndoManager.run(setExpression(source instanceof ExpressionSource expr ? expr.getBlockDefinition().createBlock() : (Expression.Block) source));
                placeholder.pseudoClassStateChanged(DRAG_OVER_STYLE_CLASS, false);
                SoundManager.SNAP.play();
                e.setDropCompleted(true);
                e.consume();
            }
        });

        placeholder.setOnDragExited(e -> {
            placeholder.pseudoClassStateChanged(DRAG_OVER_STYLE_CLASS, false);
            e.consume();
        });

        ActionMenuItem pasteItem = new ActionMenuItem(LanguageManager.get("context_menu.paste"), e -> UndoManager.run(setExpression(CopyPasteManager.pasteExpression())));
        ContextMenu contextMenu = new ContextMenu(pasteItem, new SeparatorMenuItem());
        contextMenu.setOnShowing(e -> pasteItem.setDisable(!CopyPasteManager.isExpressionCopied()));
        placeholder.setContextMenu(contextMenu);

        if (type.getClassType() == boolean.class) {
            contextMenu.getItems().addAll(
                    new ActionMenuItem("Insert Boolean", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-boolean").createBlock()))),
                    new ActionMenuItem("Insert Boolean Logic", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-boolean-logic").createBlock()))),
                    new ActionMenuItem("Insert Equals", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-is-equal").createBlock()))),
                    new ActionMenuItem("Insert Equals Ignore Case", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("java.lang.String#equalsIgnoreCase(java.lang.String)").createBlock()))));
        } else if (type.getClassType() == String.class || type.getClassType() == CharSequence.class) {
            contextMenu.getItems().addAll(
                    new ActionMenuItem("Insert String", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-string").createBlock()))),
                    new ActionMenuItem("Insert Combine Strings", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-combine-strings").createBlock()))));
        } else if (type.getClassType() == List.class) {
            contextMenu.getItems().addAll(
                    new ActionMenuItem("Insert List", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-new-list").createBlock()))));
        } else if (type.isNumber()) {
            contextMenu.getItems().addAll(
                    new ActionMenuItem("Insert Number", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-number").createBlock()))),
                    new ActionMenuItem("Insert Arithmetic", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-arithmetic").createBlock()))));
        }

        contextMenu.getItems().add(new ActionMenuItem("Insert Local Variable", e -> UndoManager.run(setExpression(BlockRegistry.getExpression("expr-local-variable").createBlock()))));
    }

    public void toggle(boolean state) {
        placeholder.pseudoClassStateChanged(CONNECTING_STYLE_CLASS, state && !isDisabled());
        if (expression != null) {
            expression.toggleExpressionParameters(state);
        }
    }

    public UndoManager.RevertableAction setExpression(Expression.Block newExpression) {
        if (newExpression == null) {
            return clear();
        }
        UndoManager.RevertableAction clearAction = clear();
        UndoManager.RevertableAction disconnectNew = newExpression.getExpressionParameter() != null ? newExpression.getExpressionParameter().clear() : UndoManager.EMPTY_ACTION;
        return new UndoManager.RevertableAction() {
            @Override
            public void run() {
                clearAction.run();
                disconnectNew.run();
                expression = newExpression;
                control.getChildren().setAll(newExpression);
                expression.update();
            }
            @Override
            public void revert() {
                expression = null;
                control.getChildren().setAll(placeholder);
                disconnectNew.revert();
                clearAction.revert();
            }
        };
    }

    public UndoManager.RevertableAction clear() {
        if (expression == null) {
            return UndoManager.EMPTY_ACTION;
        }
        Expression.Block oldExpression = expression;
        return new UndoManager.RevertableAction() {
            @Override
            public void run() {
                expression = null;
                control.getChildren().setAll(placeholder);
            }
            @Override
            public void revert() {
                expression = oldExpression;
                control.getChildren().setAll(oldExpression);
            }
        };
    }

    @Override
    public void update() {
        if (expression != null) {
            expression.update();
        }
    }

    @Override
    public void prepareBuild(BuildContext buildContext) {
        if (expression != null) {
            expression.prepareBuild(buildContext);
        }
    }

    @Override
    public String toJava() {
        return expression != null ? expression.getDefinition().getReturnType().convertTo(type, expression.toJava()) : ClassInfo.VOID.convertTo(type, "null");
    }

    @Override
    public JSONObject serialize() {
        return expression != null ? expression.serialize() : null;
    }

    @Override
    public void deserialize(Object obj) {
        if (obj instanceof JSONObject json) {
            Expression expression = BlockRegistry.getExpression(json.optString("="));
            if (expression != null) {
                setExpression(expression.createBlock(json)).run();
            }
        }
    }

    public ClassInfo getType() {
        return type;
    }

    public Expression.Block getExpression() {
        return expression;
    }
}
