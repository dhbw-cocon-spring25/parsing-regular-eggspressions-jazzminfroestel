package de.dhbw.mh.redeggs;

import static de.dhbw.mh.redeggs.CodePointRange.range;
import static de.dhbw.mh.redeggs.CodePointRange.single;

/**
 * A parser for regular expressions using recursive descent parsing.
 * This class is responsible for converting a regular expression string into a
 * tree representation of a {@link RegularEggspression}.
 */
public class RecursiveDescentRedeggsParser{

	/**
	 * The symbol factory used to create symbols for the regular expression.
	 */
	protected final SymbolFactory symbolFactory;

	/**
	 * Constructs a new {@code RecursiveDescentRedeggsParser} with the specified
	 * symbol factory.
	 *
	 * @param symbolFactory the factory used to create symbols for parsing
	 */
	public RecursiveDescentRedeggsParser(SymbolFactory symbolFactory) {
		this.symbolFactory = symbolFactory;
	}
	String input;

	/**
	 * Parses a regular expression string into an abstract syntax tree (AST).
	 * 
	 * This class uses recursive descent parsing to convert a given regular
	 * expression into a tree structure that can be processed or compiled further.
	 * The AST nodes represent different components of the regex such as literals,
	 * operators, and groups.
	 *
	 * @param regex the regular expression to parse
	 * @return the {@link RegularEggspression} representation of the parsed regex
	 * @throws RedeggsParseException if the parsing fails or the regex is invalid
	 */
	public RegularEggspression parse(String regex) throws RedeggsParseException {
		// TODO: Implement the recursive descent parsing to convert `regex` into an AST.
		// This is a placeholder implementation to demonstrate how to create a symbol.

		// Create a new symbol using the symbol factory
		this.input = regex + "#";
		/* VirtualSymbol symbol = symbolFactory.newSymbol()
				.include(single('_'), range('a', 'z'), range('A', 'Z'))
				.andNothingElse();

		// Return a dummy Literal RegularExpression for now
		return new RegularEggspression.Literal(symbol); */
		RegularEggspression result= regex();
		if (input.isEmpty()) {
			return result;
		}
		throw new RedeggsParseException("regex", 0);

	}

	public char peak() {
		return input.charAt(0);
	}

	public char consume() {
		char firstNum = peak();
		input = input.substring(1);
		return firstNum;
	}

	public void match(char compareNum) throws RedeggsParseException {
		char num = consume(); 
		if(compareNum != num) {
			throw new RedeggsParseException("Input-Char (" + num + ") does not match required Char (" + compareNum + ")", 1);
		}
	}

	public RegularEggspression regex() throws RedeggsParseException{
		RegularEggspression left = concat();
		return union(left);
	}

	public RegularEggspression union(RegularEggspression left) throws RedeggsParseException {
		char c = peak();
		if(c == '|') {
			match('|');
			RegularEggspression expr = concat();
			RegularEggspression combined = new RegularEggspression.Alternation(left, expr);
			return union(combined);
		} 
		else if(c == '#' || c == ')') {
			return left; 
		}
		throw new RedeggsParseException("Char does not match '|', '#' or ')', but '" + c + "' was read.", 1);
	}

	public RegularEggspression concat() throws RedeggsParseException {
		RegularEggspression kleen = kleene();
		return suffix(kleen);
	}

	public RegularEggspression suffix(RegularEggspression kleen) throws RedeggsParseException {
		System.out.println("Suffix: "+input);
		char c = peak(); 
		if(c == '|' || c == '#' || c == ')') {
			return kleen; 
		}
		RegularEggspression expr = kleene();
		RegularEggspression combined = new RegularEggspression.Concatenation(kleen, expr);
		return combined;
		//return suffix(combined); 
	}

	public RegularEggspression kleene() throws RedeggsParseException{
		RegularEggspression base = base();
		boolean star = star();
		if(star) {
			return new RegularEggspression.Star(base);
		}
		return base;
	}

	public boolean star() throws RedeggsParseException{
		char c = peak();
		if(c == '*') {
			match('*');
			return true;
		}
		return false;
	}

	public RegularEggspression base() throws RedeggsParseException{
		char c = peak();
		if(c == '(') {
			match('(');
			RegularEggspression regexpr = regex();
			match(')');
			return regexpr;
		} 

		consume();
		VirtualSymbol symbol = symbolFactory.newSymbol()
			.include(single(c))
			.andNothingElse();

		return new RegularEggspression.Literal(symbol);
	}
}
