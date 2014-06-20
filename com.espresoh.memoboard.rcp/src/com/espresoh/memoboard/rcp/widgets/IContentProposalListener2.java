package com.espresoh.memoboard.rcp.widgets;


public interface IContentProposalListener2 {
	/**
	 * A content proposal popup has been opened for content proposal assistance.
	 * 
	 * @param adapter
	 *            the ContentProposalAdapter which is providing content proposal
	 *            behavior to a control
	 */
	public void proposalPopupOpened(ContentProposalAdapterCustom adapter);

	/**
	 * A content proposal popup has been closed.
	 * 
	 * @param adapter
	 *            the ContentProposalAdapter which is providing content proposal
	 *            behavior to a control
	 */
	public void proposalPopupClosed(ContentProposalAdapterCustom adapter);
}
