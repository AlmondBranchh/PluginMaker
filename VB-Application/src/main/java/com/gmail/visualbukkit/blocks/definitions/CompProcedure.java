package com.gmail.visualbukkit.blocks.definitions;

import com.gmail.visualbukkit.blocks.PluginComponent;
import com.gmail.visualbukkit.blocks.parameters.StringLiteralParameter;
import com.gmail.visualbukkit.project.BuildContext;
import javafx.beans.binding.Bindings;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.List;

public class CompProcedure extends PluginComponent {

    public CompProcedure() {
        super("comp-procedure", "Procedure", "VB", "Defines a procedure");
    }

    @Override
    public Block createBlock() {
        StringLiteralParameter nameParameter = new StringLiteralParameter("Name");
        Block block = new Block(this, nameParameter) {
            @Override
            public void prepareBuild(BuildContext buildContext) {
                super.prepareBuild(buildContext);
                MethodSource<JavaClassSource> procedureMethod = buildContext.getMainClass().getMethod("procedure", String.class, List.class);
                procedureMethod.setBody(
                        "if (procedure.equalsIgnoreCase(" + arg(0) + ")) {" +
                        buildContext.getLocalVariableDeclarations() +
                        getChildJava() +
                        "return;" +
                        "}" +
                        procedureMethod.getBody());
            }
        };

        block.getTab().textProperty().bind(Bindings
                .when(nameParameter.getControl().textProperty().isNotEmpty())
                .then(Bindings.concat("Procedure - ", nameParameter.getControl().textProperty()))
                .otherwise("Procedure"));

        return block;
    }
}
