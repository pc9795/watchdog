import {MonitorType} from './models/monitor';

/**
 * Utility methods for the project
 */
export class Utils {

  static minRange = 1;
  static maxRange = 147;

  /**
   * Utility method to check that whether the enum option correspond to  HTTP Monitor.
   */
  static isHTTPMonitor(value: MonitorType): boolean {
    return value === MonitorType.HTTP_MONITOR;
  }

  /**
   * Utility method to check that whether the enum option correspond to  Socket Monitor.
   */
  static isSocketMonitor(value: MonitorType): boolean {
    return value === MonitorType.SOCKET_MONITOR;
  }

  /**
   * Format the value from time scroll bar to readable text.
   */
  static rangeToMonitoringIntervalInText(valueStr: string): string {
    const value = Number(valueStr);
    // Value between 1-5 correspond to 10-50 secs.
    if (value <= 5) {
      return `${value * 10} secs`;
    }
    // Value between 6-124 correspond to 1-119 minutes.
    if (value <= 124) {
      return `${value - 5} mins`;
    }
    // Value between 125-147 correspond to 2-24 hours.
    return `${value - 123} hours`;
  }

  /**
   * Convert the value from time scroll bar to value in seconds.
   */
  static rangeToMonitoringIntervalInSec(valueStr: number) {
    const value = Number(valueStr);
    console.log(value);
    // Value between 1-5 correspond to 10-50 secs.
    if (value <= 5) {
      return value * 10;
    }
    // Value between 6-124 correspond to 1-119 minutes.
    if (value <= 124) {
      return (value - 5) * 60;
    }
    // Value between 125-147 correspond to 2-24 hours.
    return (value - 123) * 60 * 60;
  }

  /**
   * Convert the value in seconds for a monitor to corresponding range value in scrollbar.
   */
  static monitoringIntervalInSecToRange(secs: number) {
    // Value between 10-50 secs corresponds to 1-5.
    if (secs <= 50) {
      return secs / 10;
    }
    const minutes = secs / 60;
    // Value between 1-119 minutes corresponds to 6-124.
    if (minutes <= 119) {
      return minutes + 5;
    }
    // Value between 2-24 hours corresponds to 124-147.
    return (minutes / 60) + 123;
  }

  /**
   * Convert the value in seconds for a monitor to corresponding range to a textual representation.
   */
  static monitoringIntervalInSecToMonitoringIntervalInText(secs: number | string) {
    secs = Number(secs);
    // Value between 10-50 secs corresponds to 1-5.
    if (secs <= 50) {
      return `${secs} seconds`;
    }
    const minutes = secs / 60;
    // Value between 1-119 minutes corresponds to 6-124.
    if (minutes <= 119) {
      return `${minutes} minutes`;
    }
    // Value between 2-24 hours corresponds to 124-147.
    return `${minutes / 60} hours`;
  }

}
