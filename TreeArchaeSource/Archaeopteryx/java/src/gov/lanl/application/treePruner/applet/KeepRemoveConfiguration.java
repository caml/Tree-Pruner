package gov.lanl.application.treePruner.applet;

import gov.lanl.application.treePruner.custom.data.WorkingSet;

import javax.swing.JOptionPane;

import org.forester.archaeopteryx.Configuration;
import org.forester.archaeopteryx.ControlPanel;
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
		
		if ( configuration.do_display_clickToOption( KeepRemoveConfiguration._KEEP_SEQUENCES ) ) {
            _keepSequences = s[0];
            cp.addClickToOptionKeepRemove( KeepRemoveConfiguration._KEEP_SEQUENCES, 
            							  configuration.get_click_toTitle( KeepRemoveConfiguration._KEEP_SEQUENCES ) );
            if ( defaultOption == KeepRemoveConfiguration._KEEP_SEQUENCES ) {
                s[1] = s[0];
            }
            s[0]++;
        }
		
		if ( configuration.do_display_clickToOption( KeepRemoveConfiguration._REMOVE_SEQUENCES ) ) {
            _removeSequences = s[0];
            cp.addClickToOptionKeepRemove( KeepRemoveConfiguration._REMOVE_SEQUENCES, 
            							  configuration.get_click_toTitle( KeepRemoveConfiguration._REMOVE_SEQUENCES ) );
            if ( defaultOption == KeepRemoveConfiguration._REMOVE_SEQUENCES ) {
                s[1] = s[0];
            }
            s[0]++;
        }
		return s;
	}

	private boolean isOtherSessionActive(int action) {
		if (action == _keepSequences) {
			if (WorkingSet.getRemoveActiveSequenceIds().isEmpty()) {
				return false;
			} else {
				return true;
			}
		} else if (action == _removeSequences) {
			if (WorkingSet.getkeepACC().isEmpty()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public int setClickToAction(final int action, ControlPanel cp) {
		String error = "You are attempting to change your choice of pruning action  \n";
		error += "(keep/remove/restore) without first concluding your existing pruning session.\n\n";
		error += "Please click the \"Save\", \"Discard all\" or \"Discard recent\" menu button \n";
		error += "before proceeding with this new pruning action.";
		if (!isOtherSessionActive(action)) {
			if (action == _keepSequences) {
				cp.set_action_whenNodeClicked(NodeClickAction.KEEP_SEQUENCES);
				return _keepSequences;
			} else if (action == _removeSequences) {
				cp.set_action_whenNodeClicked(NodeClickAction.REMOVE_SEQUENCES);
				return _removeSequences;
			}
		} else {
			JOptionPane.showMessageDialog(null, error, "Error Message",
					JOptionPane.ERROR_MESSAGE);
			if (action == _keepSequences) {
				cp.set_action_whenNodeClicked(NodeClickAction.REMOVE_SEQUENCES);
				return _removeSequences;
			} else if (action == _removeSequences) {
				cp.set_action_whenNodeClicked(NodeClickAction.KEEP_SEQUENCES);
				return _keepSequences;
			}
		}
		return -1;
	}

}
