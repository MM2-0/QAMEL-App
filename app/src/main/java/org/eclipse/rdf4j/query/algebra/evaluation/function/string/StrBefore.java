/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.query.algebra.evaluation.function.string;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FN;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;
import org.eclipse.rdf4j.query.algebra.evaluation.util.QueryEvaluationUtil;

/**
 * The SPARQL built-in {@link Function} STRBEFORE, as defined in
 * <a href="http://www.w3.org/TR/sparql11-query/#func-substr">SPARQL Query Language for RDF</a>.
 * 
 * @author Jeen Broekstra
 */
public class StrBefore implements Function {

	public String getURI() {
		return FN.SUBSTRING_BEFORE.toString();
	}

	public Literal evaluate(ValueFactory valueFactory, Value... args)
		throws ValueExprEvaluationException
	{
		if (args.length != 2) {
			throw new ValueExprEvaluationException(
					"Incorrect number of arguments for STRBEFORE: " + args.length);
		}

		Value leftArg = args[0];
		Value rightArg = args[1];

		if (leftArg instanceof Literal && rightArg instanceof Literal) {
			Literal leftLit = (Literal)leftArg;
			Literal rightLit = (Literal)rightArg;

			if (QueryEvaluationUtil.compatibleArguments(leftLit, rightLit)) {
				String leftLanguage = leftLit.getLanguage();
				IRI leftDt = leftLit.getDatatype();

				String lexicalValue = leftLit.getLabel();
				String substring = rightLit.getLabel();

				int index = lexicalValue.indexOf(substring);

				String substringBefore = "";
				if (index > -1) {
					substringBefore = lexicalValue.substring(0, index);
				}
				else {
					// no match, return empty string with no language or datatype
					leftLanguage = null;
					leftDt = null;
				}

				if (leftLanguage != null) {
					return valueFactory.createLiteral(substringBefore, leftLanguage);
				}
				else if (leftDt != null) {
					return valueFactory.createLiteral(substringBefore, leftDt);
				}
				else {
					return valueFactory.createLiteral(substringBefore);
				}
			}
			else {
				throw new ValueExprEvaluationException(
						"incompatible operands for STRBEFORE: " + leftArg + ", " + rightArg);
			}
		}
		else {
			throw new ValueExprEvaluationException(
					"incompatible operands for STRBEFORE: " + leftArg + ", " + rightArg);
		}
	}
}
