import { ceil125 } from "./math.js";

describe("ceil125", () => {
  it("must correctly handle 0", () => {
    expect(ceil125(0)).toEqual(1);
  });
  it("must correctly handle 1..10 range", () => {
    expect(ceil125(1)).toEqual(1);
    expect(ceil125(1.5)).toEqual(2);
    expect(ceil125(2)).toEqual(2);
    expect(ceil125(2.5)).toEqual(5);
    expect(ceil125(5)).toEqual(5);
    expect(ceil125(5.5)).toEqual(10);
    expect(ceil125(10)).toEqual(10);
  });
  it("must correctly handle 5..100 range", () => {
    expect(ceil125(10.5)).toEqual(20);
    expect(ceil125(20)).toEqual(20);
    expect(ceil125(20.5)).toEqual(50);
    expect(ceil125(50.5)).toEqual(100);
  });
  it("must correctly handle 0...1 range", () => {
    expect(ceil125(0.1)).toEqual(0.1);
    expect(ceil125(0.15)).toEqual(0.2);
    expect(ceil125(0.25)).toEqual(0.5);
    expect(ceil125(0.55)).toEqual(1);
  });
});
