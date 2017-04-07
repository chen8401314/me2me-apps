package com.me2me.search.dto;

import java.util.Map;

public class HighlightField {
        final String name;
        String[] preTags;
        String[] postTags;
        int fragmentSize = -1;
        int fragmentOffset = -1;
        int numOfFragments = -1;
        Boolean highlightFilter;
        String order;
        Boolean requireFieldMatch;
        int boundaryMaxScan = -1;
        char[] boundaryChars;
        String highlighterType;
        String fragmenter;
        Integer noMatchSize;
        String[] matchedFields;
        Integer phraseLimit;
        Map<String, Object> options;
        Boolean forceSource;

        public HighlightField(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        /**
         * Explicitly set the pre tags for this field that will be used for highlighting.
         * This overrides global settings set by {@link HighlightField#preTags(String...)}.
         */
        public HighlightField preTags(String... preTags) {
            this.preTags = preTags;
            return this;
        }

        /**
         * Explicitly set the post tags for this field that will be used for highlighting.
         * This overrides global settings set by {@link HighlightField#postTags(String...)}.
         */
        public HighlightField postTags(String... postTags) {
            this.postTags = postTags;
            return this;
        }

        public HighlightField fragmentSize(int fragmentSize) {
            this.fragmentSize = fragmentSize;
            return this;
        }

        public HighlightField fragmentOffset(int fragmentOffset) {
            this.fragmentOffset = fragmentOffset;
            return this;
        }

        public HighlightField numOfFragments(int numOfFragments) {
            this.numOfFragments = numOfFragments;
            return this;
        }

        public HighlightField highlightFilter(boolean highlightFilter) {
            this.highlightFilter = highlightFilter;
            return this;
        }

        /**
         * The order of fragments per field. By default, ordered by the order in the
         * highlighted text. Can be <tt>score</tt>, which then it will be ordered
         * by score of the fragments.
         * This overrides global settings set by {@link HighlightField#order(String)}.
         */
        public HighlightField order(String order) {
            this.order = order;
            return this;
        }

        public HighlightField requireFieldMatch(boolean requireFieldMatch) {
            this.requireFieldMatch = requireFieldMatch;
            return this;
        }

        public HighlightField boundaryMaxScan(int boundaryMaxScan) {
            this.boundaryMaxScan = boundaryMaxScan;
            return this;
        }

        public HighlightField boundaryChars(char[] boundaryChars) {
            this.boundaryChars = boundaryChars;
            return this;
        }

        /**
         * Set type of highlighter to use. Supported types
         * are <tt>highlighter</tt>, <tt>fast-vector-highlighter</tt> nad <tt>postings-highlighter</tt>.
         * This overrides global settings set by {@link HighlightField#highlighterType(String)}.
         */
        public HighlightField highlighterType(String highlighterType) {
            this.highlighterType = highlighterType;
            return this;
        }

        /**
         * Sets what fragmenter to use to break up text that is eligible for highlighting.
         * This option is only applicable when using plain / normal highlighter.
         * This overrides global settings set by {@link HighlightField#fragmenter(String)}.
         */
        public HighlightField fragmenter(String fragmenter) {
            this.fragmenter = fragmenter;
            return this;
        }


        /**
         * Sets the size of the fragment to return from the beginning of the field if there are no matches to
         * highlight.
         * @param noMatchSize integer to set or null to leave out of request.  default is null.
         * @return this for chaining
         */
        public HighlightField noMatchSize(Integer noMatchSize) {
            this.noMatchSize = noMatchSize;
            return this;
        }

        /**
         * Allows to set custom options for custom highlighters.
         * This overrides global settings set by {@link HighlightBuilder#options(Map<String, Object>)}.
         */
        public HighlightField options(Map<String, Object> options) {
            this.options = options;
            return this;
        }

        /**
         * Set the matched fields to highlight against this field data.  Default to null, meaning just
         * the named field.  If you provide a list of fields here then don't forget to include name as
         * it is not automatically included.
         */
        public HighlightField matchedFields(String... matchedFields) {
            this.matchedFields = matchedFields;
            return this;
        }

        /**
         * Sets the maximum number of phrases the fvh will consider.
         * @param phraseLimit maximum number of phrases the fvh will consider
         * @return this for chaining
         */
        public HighlightField phraseLimit(Integer phraseLimit) {
            this.phraseLimit = phraseLimit;
            return this;
        }


        /**
         * Forces the highlighting to highlight this field based on the source even if this field is stored separately.
         */
        public HighlightField forceSource(boolean forceSource) {
            this.forceSource = forceSource;
            return this;
        }

		public String[] getPreTags() {
			return preTags;
		}

		public void setPreTags(String[] preTags) {
			this.preTags = preTags;
		}

		public String[] getPostTags() {
			return postTags;
		}

		public void setPostTags(String[] postTags) {
			this.postTags = postTags;
		}

		public int getFragmentSize() {
			return fragmentSize;
		}

		public void setFragmentSize(int fragmentSize) {
			this.fragmentSize = fragmentSize;
		}

		public int getFragmentOffset() {
			return fragmentOffset;
		}

		public void setFragmentOffset(int fragmentOffset) {
			this.fragmentOffset = fragmentOffset;
		}

		public int getNumOfFragments() {
			return numOfFragments;
		}

		public void setNumOfFragments(int numOfFragments) {
			this.numOfFragments = numOfFragments;
		}

		public Boolean getHighlightFilter() {
			return highlightFilter;
		}

		public void setHighlightFilter(Boolean highlightFilter) {
			this.highlightFilter = highlightFilter;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public Boolean getRequireFieldMatch() {
			return requireFieldMatch;
		}

		public void setRequireFieldMatch(Boolean requireFieldMatch) {
			this.requireFieldMatch = requireFieldMatch;
		}

		public int getBoundaryMaxScan() {
			return boundaryMaxScan;
		}

		public void setBoundaryMaxScan(int boundaryMaxScan) {
			this.boundaryMaxScan = boundaryMaxScan;
		}

		public char[] getBoundaryChars() {
			return boundaryChars;
		}

		public void setBoundaryChars(char[] boundaryChars) {
			this.boundaryChars = boundaryChars;
		}

		public String getHighlighterType() {
			return highlighterType;
		}

		public void setHighlighterType(String highlighterType) {
			this.highlighterType = highlighterType;
		}

		public String getFragmenter() {
			return fragmenter;
		}

		public void setFragmenter(String fragmenter) {
			this.fragmenter = fragmenter;
		}

		public Integer getNoMatchSize() {
			return noMatchSize;
		}

		public void setNoMatchSize(Integer noMatchSize) {
			this.noMatchSize = noMatchSize;
		}

		public String[] getMatchedFields() {
			return matchedFields;
		}

		public void setMatchedFields(String[] matchedFields) {
			this.matchedFields = matchedFields;
		}

		public Integer getPhraseLimit() {
			return phraseLimit;
		}

		public void setPhraseLimit(Integer phraseLimit) {
			this.phraseLimit = phraseLimit;
		}

		public Map<String, Object> getOptions() {
			return options;
		}

		public void setOptions(Map<String, Object> options) {
			this.options = options;
		}

		public Boolean getForceSource() {
			return forceSource;
		}

		public void setForceSource(Boolean forceSource) {
			this.forceSource = forceSource;
		}

		public String getName() {
			return name;
		}

    }