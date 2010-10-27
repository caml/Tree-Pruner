package com.lanl.application.treeDecorator.enumeration;

public enum DecorationStyles {
	
	NULL("None"),
	
	//colors
	EGGPLANT("Eggplant"),
	DARK_GRAY("Dark Gray"),
	BROWN("Brown"),
	BLACK("Black"),
	TANGERINE("Tangerine"),
	RED("Red"),
	PINK("Pink"),
	BLUE("Blue"),
	DARK_KHAKI("Dark Khaki"),
	FIRE_BRICK("Fire Brick"),
	DARK_MAGENTA("Dark Magenta"),
	NAVY("Navy"),
	STEEL_BLUE("Steel Blue"),
	FOREST_GREEN("Forest Green"),
	AQUA("Aqua"),
	LIME("Lime"),
	
	//SHAPES
	HOLLOW_TRIANGE("Hollow Triangle"),
	FILLED_TRIANGE("Filled Triangle"),
	HOLLOW_CIRCLE("Hollow Circle"),
	FILLED_CIRCLE("Filled Circle"),
	HOLLOW_INVERTED_TRIANGE("Hollow Inverted Triangle"),
	FILLED_INVERTED_TRIANGE("Filled Inverted Triange"),
	HOLLOW_DIAMOND("Hollow Diamond"),
	FILLED_DIAMOND("Filled Diamond"),
	HOLLOW_SQUARE("Hollow Square"),
	FILLED_SQUARE("Filled Square"),
	
	//case
	AS_IS("As Is"),
	LOWER("lower case"),
	UPPER("UPPER CASE"),
	
	//Fonts
	ARIAL("Arial"),
	CHALKBOARD("chalkboard"),
	VERANDA ("Verdana"),
	HELVETICA("Helvetica"),
	TIMES_NEW_ROMAN ("Times New Roman"),
	COURIER("Courier"),
	COMIC_SANS_MS("Comic Sans MS"),
	
	//Style
	REGULAR("0"),
	BOLD("1"),
	ITALIC("2"),
	UNDERLINE("10"),
	
	//Size
	_8("8"),
	_9("9"),
	_10("10"),
	_12("12"),
	_14("14");
	
	
	
	private final String name; 
	DecorationStyles(String type){
		this.name = type;
	}
	
	public String getName(){
		return this.name;
	}
}
