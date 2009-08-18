package com.lanl.application.treeDecorator.dataStructures;

import java.awt.Color;

import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;



public class DecorateObject {
	DecorationStyles _strainColor,_case,_font,_style,_size;
	DecorationStyles _nodeColor,_shapes;
		
	public DecorateObject(){
		_shapes = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SHAPES);
		_nodeColor = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.NODE_COLOR);
		
		_strainColor = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STRAIN_COLOR); 
		_case = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.CASE);
		_font = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.FONT);
		_style = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STYLE);
		_size = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SIZE);
	}
	
	public DecorateObject(DecorationStyles shapes,DecorationStyles nodeColor,DecorationStyles strainColor
			,DecorationStyles font,DecorationStyles Case,DecorationStyles style,DecorationStyles size){
		_shapes = shapes; 
		_nodeColor = nodeColor;
		
		_strainColor = strainColor;  
		_case =  Case;
		_font =  font;
		_style = style;
		_size = size;
		
	}
	
	public DecorationStyles getNodeColor(){
		return _nodeColor;
	}
	
	public DecorationStyles getNodeShape(){
		return _shapes;
	}
	public DecorationStyles getStrainColor(){
		return _strainColor;
	}
	
	public DecorationStyles getStrainCase(){
		return _case;
	}
	public DecorationStyles getStrainStyle(){
		return _style;
	}
	public DecorationStyles getStrainFont(){
		return _font;
	}
	public DecorationStyles getStrainSize(){
		return _size;
	}
	
	public void setAnyDecorationStyle(DecoratorUIConstants styleName,DecorationStyles styleValue){
		if(styleName == DecoratorUIConstants.NODE_COLOR){
			this._nodeColor = styleValue;
		}
		else if(styleName == DecoratorUIConstants.SHAPES){
			this._shapes = styleValue;
		}
		else if(styleName == DecoratorUIConstants.STRAIN_COLOR){
			this._strainColor = styleValue;
		}
		else if(styleName == DecoratorUIConstants.CASE){
			this._case = styleValue;
		}
		else if(styleName == DecoratorUIConstants.STYLE){
			this._style = styleValue;
		}
		else if(styleName == DecoratorUIConstants.FONT){
			this._font = styleValue;
		}
		else if(styleName == DecoratorUIConstants.SIZE){
			this._size = styleValue;
		}
		
	}
	
	public DecorationStyles getAnyDecorationStyle(DecoratorUIConstants styleName){
		if(styleName == DecoratorUIConstants.NODE_COLOR){
			return this._nodeColor;
		}
		else if(styleName == DecoratorUIConstants.SHAPES){
			return this._shapes;
		}
		else if(styleName == DecoratorUIConstants.STRAIN_COLOR){
			return this._strainColor;
		}
		else if(styleName == DecoratorUIConstants.CASE){
			return this._case;
		}
		else if(styleName == DecoratorUIConstants.STYLE){
			return this._style;
		}
		else if(styleName == DecoratorUIConstants.FONT){
			return this._font;
		}
		else if(styleName == DecoratorUIConstants.SIZE){
			return this._size;
		}
		else throw new IllegalArgumentException("The style name is unknown to the Tree Decorator");
		
	}
	
	@Override
	public String toString(){
		return "\nNode Color = "+_nodeColor+"\nShape = "+_shapes+"\nStrain Color = "+_strainColor+
				"\nCase = "+_case+"\nFont = "+_font+"\nStyle = "+_style+"\nSize = "+_size+"\n";
	}
	
	public boolean isEqual(DecorateObject decorateObject){
		if(decorateObject.getNodeColor() == this.getNodeColor()&&
				decorateObject.getNodeShape() == this.getNodeShape()&&
				decorateObject.getStrainCase() == this.getStrainCase()&&
				decorateObject.getStrainColor() == this.getStrainColor()&&
				decorateObject.getStrainFont() == this.getStrainFont()&&
				decorateObject.getStrainSize() == this.getStrainSize()&&
				decorateObject.getStrainStyle() == this.getStrainStyle()){
			return true;
		}
		else return false;
	}
}
