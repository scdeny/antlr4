/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.Arrays;

/**
 * This class provides a default implementation of the {@link Vocabulary}
 * interface.
 *
 * @author Sam Harwell
 */
public class VocabularyImpl implements Vocabulary {
	private static final String[] EMPTY_NAMES = new String[0];

	/**
	 * Gets an empty {@link Vocabulary} instance.
	 *
	 * <p>
	 * No literal or symbol names are assigned to token types, so
	 * {@link #getDisplayName(int)} returns the numeric value for all tokens
	 * except {@link Token#EOF}.</p>
	 */
	public static final VocabularyImpl EMPTY_VOCABULARY = new VocabularyImpl(EMPTY_NAMES, EMPTY_NAMES, EMPTY_NAMES);


	private final String[] literalNames;

	private final String[] symbolicNames;

	private final String[] displayNames;

	/**
	 * Constructs a new instance of {@link VocabularyImpl} from the specified
	 * literal and symbolic token names.
	 *
	 * @param literalNames The literal names assigned to tokens, or {@code null}
	 * if no literal names are assigned.
	 * @param symbolicNames The symbolic names assigned to tokens, or
	 * {@code null} if no symbolic names are assigned.
	 *
	 * @see #getLiteralName(int)
	 * @see #getSymbolicName(int)
	 */
	public VocabularyImpl(String[] literalNames, String[] symbolicNames) {
		this(literalNames, symbolicNames, null);
	}

	/**
	 * Constructs a new instance of {@link VocabularyImpl} from the specified
	 * literal, symbolic, and display token names.
	 *
	 * @param literalNames The literal names assigned to tokens, or {@code null}
	 * if no literal names are assigned.
	 * @param symbolicNames The symbolic names assigned to tokens, or
	 * {@code null} if no symbolic names are assigned.
	 * @param displayNames The display names assigned to tokens, or {@code null}
	 * to use the values in {@code literalNames} and {@code symbolicNames} as
	 * the source of display names, as described in
	 * {@link #getDisplayName(int)}.
	 *
	 * @see #getLiteralName(int)
	 * @see #getSymbolicName(int)
	 * @see #getDisplayName(int)
	 */
	public VocabularyImpl(String[] literalNames, String[] symbolicNames, String[] displayNames) {
		this.literalNames = literalNames != null ? literalNames : EMPTY_NAMES;
		this.symbolicNames = symbolicNames != null ? symbolicNames : EMPTY_NAMES;
		this.displayNames = displayNames != null ? displayNames : EMPTY_NAMES;
	}

	/**
	 * Returns a {@link VocabularyImpl} instance from the specified set of token
	 * names. This method acts as a compatibility layer for the single
	 * {@code tokenNames} array generated by previous releases of ANTLR.
	 *
	 * <p>The resulting vocabulary instance returns {@code null} for
	 * {@link #getLiteralName(int)} and {@link #getSymbolicName(int)}, and the
	 * value from {@code tokenNames} for the display names.</p>
	 *
	 * @param tokenNames The token names, or {@code null} if no token names are
	 * available.
	 * @return A {@link Vocabulary} instance which uses {@code tokenNames} for
	 * the display names of tokens.
	 */
	public static Vocabulary fromTokenNames(String[] tokenNames) {
		if (tokenNames == null || tokenNames.length == 0) {
			return EMPTY_VOCABULARY;
		}

		String[] literalNames = Arrays.copyOf(tokenNames, tokenNames.length);
		String[] symbolicNames = Arrays.copyOf(tokenNames, tokenNames.length);
		for (int i = 0; i < tokenNames.length; i++) {
			String tokenName = tokenNames[i];
			if (tokenName == null) {
				continue;
			}

			if (!tokenName.isEmpty()) {
				char firstChar = tokenName.charAt(0);
				if (firstChar == '\'') {
					symbolicNames[i] = null;
					continue;
				}
				else if (Character.isUpperCase(firstChar)) {
					literalNames[i] = null;
					continue;
				}
			}

			// wasn't a literal or symbolic name
			literalNames[i] = null;
			symbolicNames[i] = null;
		}

		return new VocabularyImpl(literalNames, symbolicNames, tokenNames);
	}

	@Override
	public String getLiteralName(int tokenType) {
		if (tokenType >= 0 && tokenType < literalNames.length) {
			return literalNames[tokenType];
		}

		return null;
	}

	@Override
	public String getSymbolicName(int tokenType) {
		if (tokenType >= 0 && tokenType < symbolicNames.length) {
			return symbolicNames[tokenType];
		}

		if (tokenType == Token.EOF) {
			return "EOF";
		}

		return null;
	}

	@Override
	public String getDisplayName(int tokenType) {
		if (tokenType >= 0 && tokenType < displayNames.length) {
			String displayName = displayNames[tokenType];
			if (displayName != null) {
				return displayName;
			}
		}

		String literalName = getLiteralName(tokenType);
		if (literalName != null) {
			return literalName;
		}

		String symbolicName = getSymbolicName(tokenType);
		if (symbolicName != null) {
			return symbolicName;
		}

		return Integer.toString(tokenType);
	}
}
