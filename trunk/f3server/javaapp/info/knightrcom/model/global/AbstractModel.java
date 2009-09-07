package info.knightrcom.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <PT>
 * @param <CT>
 */
public  class AbstractModel<PT, CT> {

    /**
     * @return
     */
    public static class DummyParentModel implements ParentModel {
    }

    /**
     * @return
     */
    public static class DummyChildModel implements ChildModel {
    }

    protected String id;

    protected String name;

    protected String parentId;

    protected PT parent;

    protected final String modelCategory;

    protected String displayIndex;
    
    protected String disabled;

    protected int childLimit = -1;

    /**
     * key => id, value => child item
     */
    protected Map<String, CT> childContainer = Collections.synchronizedMap(new HashMap<String, CT>());

    /**
     * 
     */
    public AbstractModel() {
        String className = this.getClass().getSimpleName();
        String packageName = this.getClass().getPackage().toString();
        modelCategory = className.replaceAll(packageName + ".", "");
    }

    /**
     * @return the id
     */
    public synchronized String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public synchronized void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public synchronized void setName(String name) {
        this.name = name;
    }

    /**
     * @return the itemContainer
     */
    public Map<String, CT> getChildren() {
        return childContainer;
    }

    /**
     * @return the itemContainer
     */
    public CT getChild(String key) {
        return childContainer.get(key);
    }

    /**
     * @param childContainer the itemContainer to set
     */
    public void addChild(String key, CT value) {
        if (childContainer.containsKey(key)) {
            System.err.println(key + "已经存在！！！");
            return;
        }
        childContainer.put(key, value);
    }

    /**
     * @param childContainer the itemContainer to set
     */
    public void removeChild(String key) {
        if (!childContainer.containsKey(key)) {
            System.err.println(key + "不存在！！！");
            return;
        }
        childContainer.remove(key);
    }

    /**
     * @return
     */
    public int getChildSize() {
        return childContainer.entrySet().size();
    }

    /**
     * @return the parent
     */
    public synchronized PT getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public synchronized void setParent(PT parent) {
        this.parent = parent;
    }

    /**
     * @return
     */
    public String getModelCatagory() {
        return this.modelCategory;
    }

    /**
     * @return the childLimit
     */
    public int getChildLimit() {
        return childLimit;
    }

    /**
     * @param childLimit the childLimit to set
     */
    public void setChildLimit(int childLimit) {
        this.childLimit = childLimit;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the displayIndex
     */
    public String getDisplayIndex() {
        return displayIndex;
    }

    /**
     * @param displayIndex the displayIndex to set
     */
    public void setDisplayIndex(String displayIndex) {
        this.displayIndex = displayIndex;
    }

	/**
	 * @return the disabled
	 */
	public String getDisabled() {
		return disabled;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

}
