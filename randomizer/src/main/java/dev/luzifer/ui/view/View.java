package dev.luzifer.ui.view;

import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public abstract class View<T extends ViewModel> extends Stage implements Initializable {
    private final T viewModel;
    
    protected View(T viewModel) {
        this.viewModel = viewModel;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    
    public T getViewModel() {
        return viewModel;
    }
    
    /**
     * Change the parent of a node.
     *
     * <p>
     * The node should have a common ancestor with the new parent.
     * </p>
     *
     * @param item
     *            The node to move.
     * @param newParent
     *            The new parent.
     */
    @SuppressWarnings("unchecked")
    protected void changeParent(Node item, Parent newParent) {
        
        if(item.getParent().equals(newParent))
            return;
        
        try {
            // HAve to use reflection, because the getChildren method is protected in common ancestor of all
            // parent nodes.
            
            // Checking old parent for public getChildren() method
            Parent oldParent = item.getParent();
            if ((oldParent.getClass().getMethod("getChildren").getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                throw new IllegalArgumentException("Old parent has no public getChildren method.");
            }
            // Checking new parent for public getChildren() method
            if ((newParent.getClass().getMethod("getChildren").getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                throw new IllegalArgumentException("New parent has no public getChildren method.");
            }
            
            // Finding common ancestor for the two parents
            Parent commonAncestor = findCommonAncestor(oldParent, newParent);
            if (commonAncestor == null) {
                throw new IllegalArgumentException("Item has no common ancestor with the new parent.");
            }
            
            // Bounds of the item
            Bounds itemBoundsInParent = item.getBoundsInParent();
            
            // Mapping coordinates to common ancestor
            Bounds boundsInParentBeforeMove = localToParentRecursive(oldParent, commonAncestor, itemBoundsInParent);
            
            // Swapping parent
            ((Collection<Node>) oldParent.getClass().getMethod("getChildren").invoke(oldParent)).remove(item);
            ((Collection<Node>) newParent.getClass().getMethod("getChildren").invoke(newParent)).add(item);
            
            // Mapping coordinates back from common ancestor
            Bounds boundsInParentAfterMove = parentToLocalRecursive(newParent, commonAncestor, boundsInParentBeforeMove);
            
            // Setting new translation
            item.setTranslateX(
                    item.getTranslateX() + (boundsInParentAfterMove.getMinX() - itemBoundsInParent.getMinX()));
            item.setTranslateY(
                    item.getTranslateY() + (boundsInParentAfterMove.getMinY() - itemBoundsInParent.getMinY()));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException e) {
            throw new IllegalStateException("Error while switching parent.", e);
        }
    }
    
    /**
     * Finds the topmost common ancestor of two nodes.
     *
     * @param firstNode
     *            The first node to check.
     * @param secondNode
     *            The second node to check.
     * @return The common ancestor or null if the two node is on different
     *         parental tree.
     */
    protected Parent findCommonAncestor(Node firstNode, Node secondNode) {
        // Builds up the set of all ancestor of the first node.
        Set<Node> parentalChain = new HashSet<>();
        Node cn = firstNode;
        while (cn != null) {
            parentalChain.add(cn);
            cn = cn.getParent();
        }
        
        // Iterates down through the second ancestor for common node.
        cn = secondNode;
        while (cn != null) {
            if (parentalChain.contains(cn)) {
                return (Parent) cn;
            }
            cn = cn.getParent();
        }
        return null;
    }
    
    /**
     * Transitively converts the coordinates from the node to an ancestor's
     * coordinate system.
     *
     * @param node
     *            The node the starting coordinates are local to.
     * @param ancestor
     *            The ancestor to map the coordinates to.
     * @param x
     *            The X of the point to be converted.
     * @param y
     *            The Y of the point to be converted.
     * @return The converted coordinates.
     */
    protected Point2D localToParentRecursive(Node node, Parent ancestor, double x, double y) {
        Point2D p = new Point2D(x, y);
        Node cn = node;
        while (cn != null) {
            if (cn == ancestor) {
                return p;
            }
            p = cn.localToParent(p);
            cn = cn.getParent();
        }
        throw new IllegalStateException("The node is not a descedent of the parent.");
    }
    
    /**
     * Transitively converts the coordinates of a bound from the node to an
     * ancestor's coordinate system.
     *
     * @param node
     *            The node the starting coordinates are local to.
     * @param ancestor
     *            The ancestor to map the coordinates to.
     * @param bounds
     *            The bounds to be converted.
     * @return The converted bounds.
     */
    protected Bounds localToParentRecursive(Node node, Parent ancestor, Bounds bounds) {
        Point2D p = localToParentRecursive(node, ancestor, bounds.getMinX(), bounds.getMinY());
        return new BoundingBox(p.getX(), p.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    /**
     * Transitively converts the coordinates from an ancestor's coordinate
     * system to the nodes local.
     *
     * @param x
     *            The X of the point to be converted.
     * @param y
     *            The Y of the point to be converted.
     * @return The converted coordinates.
     */
    public static Point2D parentToLocalRecursive(Node n, Parent parent, double x, double y) {
        List<Node> parentalChain = new ArrayList<>();
        Node cn = n;
        while (cn != null) {
            if (cn == parent) {
                break;
            }
            parentalChain.add(cn);
            cn = cn.getParent();
        }
        if (cn == null) {
            throw new IllegalStateException("The node is not a descedent of the parent.");
        }
        
        Point2D p = new Point2D(x, y);
        for (int i = parentalChain.size() - 1; i >= 0; i--) {
            p = parentalChain.get(i).parentToLocal(p);
        }
        
        return p;
    }
    
    /**
     * Transitively converts the coordinates of the bounds from an ancestor's
     * coordinate system to the nodes local.
     *
     * @param bounds
     *            The bounds to be converted.
     * @return The converted coordinates.
     */
    public static Bounds parentToLocalRecursive(Node n, Parent parent, Bounds bounds) {
        Point2D p = parentToLocalRecursive(n, parent, bounds.getMinX(), bounds.getMinY());
        return new BoundingBox(p.getX(), p.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    /**
     * Tr
    
    /**
     * This method does set an {@link ImagePattern} as fill of a {@link Shape}.
     * Means the background is being set.
     */
    protected void setFillImage(Shape shape, String imageName) {
        
        URL url = View.class.getClassLoader().getResource("images/logo.png");
        if (url == null)
            throw new IllegalStateException(MessageFormat.format("A resource with this name could not be found: {0}", imageName));
        
        Image image = getImageByURL(fetchResourceAsURL(imageName));
        shape.setFill(new ImagePattern(image));
    }
    
    /**
     * This method sets the {@link BackgroundImage} of a {@link Region}, means Buttons, Panes, Fields etc.
     */
    protected void setBackgroundImage(Region region, String imageName) {
        region.setBackground(new Background(convertPNGToBackgroundImage(imageName)));
    }
    
    private BackgroundImage convertPNGToBackgroundImage(String name) {
        
        Image image = getImageByURL(fetchResourceAsURL(name));
        
        return new BackgroundImage(
                image,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        );
    }
    
    private Image getImageByURL(URL url) {
        return new Image(url.toExternalForm());
    }
    
    private URL fetchResourceAsURL(String name) {
        
        URL url = View.class.getClassLoader().getResource(name);
        if (url == null)
            throw new IllegalStateException(MessageFormat.format("A resource with this name could not be found: {0}", name));
        
        return url;
    }
}
