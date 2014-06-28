package com.dabi.habitv.tray.controller.todl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.scene.control.CheckBoxTreeItem;

import com.dabi.habitv.api.plugin.exception.TechnicalException;

public class CompatibleCheckBoxTreeItem<E> extends CheckBoxTreeItem<E> {

	private boolean compatiblityMode = false;

	public CompatibleCheckBoxTreeItem(E value) {
		super(value);
	}

	public void setVSelected(boolean selected) {
		if (compatiblityMode) {
			invoke(this, "setSelected", selected);
		} else {
			try {
				super.setSelected(selected);
			} catch (NoSuchMethodError e) {
				compatiblityMode = true;
				setVSelected(selected);
			}
		}
	}

	public void setVIndeterminate(boolean indeterminate) {
		if (compatiblityMode) {
			invoke(this, "setIndeterminate", indeterminate);
		} else {
			try {
				super.setIndeterminate(indeterminate);
			} catch (NoSuchMethodError e) {
				compatiblityMode = true;
				setVIndeterminate(indeterminate);
			}
		}
	}

	public void setVIndependant(boolean independant) {
		if (compatiblityMode) {
			invoke(this, "setIndependent", independant);
		} else {
			try {
				super.setIndependent(independant);
			} catch (NoSuchMethodError e) {
				compatiblityMode = true;
				setVIndependant(independant);
			}
		}
	}

	public boolean isVIndeterminate() {
		if (compatiblityMode) {
			return (Boolean) invoke(this, "isIndeterminate", null);
		} else {
			try {
				return super.isIndeterminate();
			} catch (NoSuchMethodError e) {
				compatiblityMode = true;
				return isVIndeterminate();
			}
		}
	}

	public boolean isVSelected() {
		if (compatiblityMode) {
			return (Boolean) invoke(this, "isSelected", null);
		} else {
			try {
				return super.isSelected();
			} catch (NoSuchMethodError e) {
				compatiblityMode = true;
				return isVIndeterminate();
			}
		}
	}

	private Object invoke(final Object category, String methodName,
			final Boolean var) {
		Class<?> types[];
		if (var == null) {
			types = new Class[] {};
		} else {
			types = new Class[] { Boolean.TYPE };
		}
		Method method;
		try {
			method = category.getClass().getMethod(methodName, types);
			Object[] parametres;
			if (var == null) {
				parametres = new Object[] {};
			} else {
				parametres = new Object[] { var };
			}
			return method.invoke(category, parametres);

		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			try {
				method = category.getClass().getMethod(methodName,
						Boolean.class);
				Object parametres[] = { var };
				return method.invoke(category, parametres);
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e1) {
				throw new TechnicalException(e1);
			}
		}
	}

}
