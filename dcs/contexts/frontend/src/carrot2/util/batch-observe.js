import { observe } from '@nx-js/observer-util';


const BatchScheduler = function () {
  const reactions = new Set();
  this.add = (reaction) => {
    reactions.add(reaction);
    requestAnimationFrame(() => {
      reactions.forEach(reaction => {
        return reaction();
      });
      reactions.clear();
    });
  };

  this.delete = (reaction) => {
    reactions.delete(reaction);
  }
};

export const observeBatched = (fn) => {
  observe(fn, { scheduler: new BatchScheduler() });
};