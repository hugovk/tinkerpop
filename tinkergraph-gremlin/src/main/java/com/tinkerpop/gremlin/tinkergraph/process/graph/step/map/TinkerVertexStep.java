package com.tinkerpop.gremlin.tinkergraph.process.graph.step.map;

import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.graph.step.map.VertexStep;
import com.tinkerpop.gremlin.structure.Direction;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerHelper;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerVertex;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TinkerVertexStep<E extends Element> extends VertexStep<E> {

    public TinkerVertexStep(final Traversal traversal, final Class<E> returnClass, final Direction direction, final int branchFactor, final String... edgeLabels) {
        super(traversal, returnClass, direction, branchFactor, edgeLabels);
        if (Vertex.class.isAssignableFrom(returnClass))
            this.setFunction(traverser -> (Iterator) TinkerHelper.getVertices((TinkerVertex) traverser.get(), this.direction, this.branchFactor, this.edgeLabels));
        else
            this.setFunction(traverser -> (Iterator) TinkerHelper.getEdges((TinkerVertex) traverser.get(), this.direction, this.branchFactor, this.edgeLabels));
    }
}