import { store } from "@risingstack/react-easy-state";

export const errors = store({
  current: null,
  last: null,
  addError: e => {
    // Currently we overwrite the previous error. In the future we can build a UI for showing
    // all errors that occurred since the last error was dismissed.
    errors.current = e;
  },
  dismiss: () => {
    errors.last = errors.current;
    errors.current = null;
  }
});
