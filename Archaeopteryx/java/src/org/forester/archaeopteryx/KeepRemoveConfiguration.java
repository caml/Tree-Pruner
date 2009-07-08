package org.forester.archaeopteryx;

import org.forester.archaeopteryx.ControlPanel.NodeClickAction;





public class KeepRemoveConfiguration {
	public Configuration configuration;
	public int _keepSequences;
	public int _removeSequences;
	public final static int  _KEEP_SEQUENCES                 = 6;
    public final static int  _REMOVE_SEQUENCES            	 = 7;
    public KeepRemoveConfiguration(Configuration conf){
    	this.configuration = conf;
    }
	public int getClickToIndex(String name, int index) {
		  if ( name.equals( "_KEEP_SEQUENCES" ) ) {
	            index = KeepRemoveConfiguration._KEEP_SEQUENCES;
	        }
		  else if ( name.equals( "_REMOVE_SEQUENCES" ) ) {
	            index = KeepRemoveConfiguration._REMOVE_SEQUENCES;
	        }
		return index;
	}
	public int[] setupClickToOptions(int[] s, int defaultOption, ControlPanel cp) {
		
		if ( configuration.doDisplayClickToOption( KeepRemoveConfiguration._KEEP_SEQUENCES ) ) {
            _keepSequences = s[0];
            cp.addClickToOptionKeepRemove( KeepRemoveConfiguration._KEEP_SEQUENCES, 
            							  configuration.getClickToTitle( KeepRemoveConfiguration._KEEP_SEQUENCES ) );
            if ( defaultOption == KeepRemoveConfiguration._KEEP_SEQUENCES ) {
                s[1] = s[0];
            }
            s[0]++;
        }
		
		if ( configuration.doDisplayClickToOption( KeepRemoveConfiguration._REMOVE_SEQUENCES ) ) {
            _removeSequences = s[0];
            cp.addClickToOptionKeepRemove( KeepRemoveConfiguration._REMOVE_SEQUENCES, 
            							  configuration.getClickToTitle( KeepRemoveConfiguration._REMOVE_SEQUENCES ) );
            if ( defaultOption == KeepRemoveConfiguration._REMOVE_SEQUENCES ) {
                s[1] = s[0];
            }
            s[0]++;
        }
		return s;
	}
	public void setClickToAction( final int action, ControlPanel cp) {
		if(action == _keepSequences ){
			cp.setActionWhenNodeClicked( NodeClickAction.KEEP_SEQUENCES );
		}
		else if(action == _removeSequences){
			cp.setActionWhenNodeClicked( NodeClickAction.REMOVE_SEQUENCES );
		}
	}
}
